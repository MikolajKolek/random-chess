package pl.edu.uj.tcs.rchess.server

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.json.Json
import org.jooq.JSONB
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import pl.edu.uj.tcs.rchess.config.BotType
import pl.edu.uj.tcs.rchess.config.Config
import pl.edu.uj.tcs.rchess.generated.db.keys.SERVICE_GAMES__SERVICE_GAMES_SERVICE_ID_BLACK_PLAYER_FKEY
import pl.edu.uj.tcs.rchess.generated.db.keys.SERVICE_GAMES__SERVICE_GAMES_SERVICE_ID_WHITE_PLAYER_FKEY
import pl.edu.uj.tcs.rchess.generated.db.tables.references.PGN_GAMES
import pl.edu.uj.tcs.rchess.generated.db.tables.references.SERVICE_ACCOUNTS
import pl.edu.uj.tcs.rchess.generated.db.tables.references.SERVICE_GAMES
import pl.edu.uj.tcs.rchess.model.*
import pl.edu.uj.tcs.rchess.model.Fen.Companion.fromFen
import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString
import pl.edu.uj.tcs.rchess.model.state.BoardState
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import pl.edu.uj.tcs.rchess.model.state.GameState
import pl.edu.uj.tcs.rchess.server.game.HistoryGame
import pl.edu.uj.tcs.rchess.server.game.HistoryServiceGame
import pl.edu.uj.tcs.rchess.server.game.LiveGame
import pl.edu.uj.tcs.rchess.server.game.PgnGame
import pl.edu.uj.tcs.rchess.util.tryWithLock
import java.sql.DriverManager
import java.time.LocalDateTime
import java.util.*

class Server(private val config: Config) : ClientApi, Database {
    private val connection = DriverManager.getConnection(
        "jdbc:postgresql://${config.database.host}:${config.database.port}/${config.database.database}",
        config.database.user,
        config.database.password
    )
    private val dsl = DSL.using(connection, SQLDialect.POSTGRES)
    private val botOpponents: Map<BotOpponent, BotType>
    private val botGameFactory = GameWithBotFactory(this)

    private val syncMutex = Mutex()
    override val databaseState = MutableStateFlow(ClientApi.DatabaseState(
        updatesAvailable = false,
        synchronizing = false
    ))

    init {
        val botServiceAccounts = dsl.selectFrom(SERVICE_ACCOUNTS)
            .where(SERVICE_ACCOUNTS.IS_BOT.eq(true))
            .and(SERVICE_ACCOUNTS.SERVICE_ID.eq(Service.RANDOM_CHESS.id))
            .fetch()

        botOpponents = config.bots
            .associateWith {
                botType -> botServiceAccounts.firstOrNull { sa -> sa.userIdInService == botType.serviceAccountId }
                    ?: throw IllegalStateException(
                        "Bot with id ${botType.serviceAccountId} does not exist in the database"
                    )
            }
            .map { (botType, serviceAccountRecord) -> BotOpponent(
                serviceAccountRecord.displayName,
                botType.description,
                botType.elo,
                serviceAccountRecord.userIdInService,) to botType
            }.associate { it }

        requestResyncImpl()
    }


    override suspend fun getUserGames(refreshAvailableUpdates: Boolean): List<HistoryGame> =
        (serviceGamesRequest(Optional.empty()) + pgnGamesRequest(Optional.empty()))
            .sortedByDescending { it.creationDate }

    override suspend fun getServiceGame(id: Int): HistoryServiceGame {
        return serviceGamesRequest(Optional.of(id)).firstOrNull() ?: throw IllegalArgumentException(
            "Game with id $id does not exist or is owned by another user"
        )
    }

    override suspend fun getPgnGame(id: Int): PgnGame {
        return pgnGamesRequest(Optional.of(id)).firstOrNull() ?: throw IllegalArgumentException(
           "Game with id $id does not exist or is owned by another user"
        )
    }

    override suspend fun addPgnGames(fullPgn: String): List<Int> {
        val result = mutableListOf<Int>()
        val pgnList = Pgn.fromPgnDatabase(fullPgn)
        dsl.transaction { transaction ->
            for(pgn in pgnList) {
                result.add(transaction.dsl().insertInto(PGN_GAMES)
                    .set(PGN_GAMES.MOVES, pgn.moves.map { it.toLongAlgebraicNotation() }.toTypedArray())
                    .set(PGN_GAMES.STARTING_POSITION, pgn.startingPosition.toFenString())
                    .set(PGN_GAMES.CREATION_DATE, LocalDateTime.now())
                    .set(PGN_GAMES.RESULT, pgn.result.toDbResult())
                    .set(PGN_GAMES.METADATA, JSONB.jsonb(Json.encodeToString(pgn.metadata)))
                    .set(PGN_GAMES.OWNER_ID, config.defaultUser)
                    .set(PGN_GAMES.BLACK_PLAYER_NAME, pgn.blackPlayerName)
                    .set(PGN_GAMES.WHITE_PLAYER_NAME, pgn.whitePlayerName)
                    .returningResult(PGN_GAMES.ID)
                    .fetchOne()?.getValue(PGN_GAMES.ID)
                    ?: throw IllegalStateException("Failed to insert PGN into database")
                )
            }
        }

        return result
    }

    override suspend fun getSystemAccount(): ServiceAccount {
        return dsl.selectFrom(SERVICE_ACCOUNTS)
            .where(SERVICE_ACCOUNTS.USER_ID.eq(config.defaultUser))
            .and(SERVICE_ACCOUNTS.SERVICE_ID.eq(Service.RANDOM_CHESS.id))
            .fetch().firstOrNull()?.let {
                ServiceAccount(
                    Service.RANDOM_CHESS,
                    it.userIdInService,
                    it.displayName,
                    it.isBot,
                    true
                )
            } ?: throw IllegalStateException("The system account does not exist")
    }

    override suspend fun getBotOpponents(): List<BotOpponent> {
        return botOpponents.keys.toList().sortedBy { it.elo }
    }

    override suspend fun startGameWithBot(
        playerColor: PlayerColor?,
        botOpponent: BotOpponent,
        clockSettings: ClockSettings
    ): LiveGame {
        val finalPlayerColor = playerColor ?: listOf(PlayerColor.WHITE, PlayerColor.BLACK).random()

        val controls = botGameFactory.createAndStart(
            finalPlayerColor,
            playerServiceAccountId = config.defaultUser.toString(),
            botType = botOpponents[botOpponent]
                ?: throw IllegalArgumentException("The provided bot opponent does not exist"),
            clockSettings = clockSettings,
            coroutineScope = MainScope()
        )

        return LiveGame(
            controls = controls
        )
    }

    override suspend fun requestResync() {
        requestResyncImpl()
    }

    fun requestResyncImpl() {
        syncMutex.tryWithLock {

        }
    }

    private fun serviceGamesRequest(id: Optional<Int>): List<HistoryServiceGame> {
        val whiteAccount = SERVICE_ACCOUNTS.`as`("white_account")
        val blackAccount = SERVICE_ACCOUNTS.`as`("black_account")

        var query = dsl.select(SERVICE_GAMES, whiteAccount, blackAccount)
            .from(SERVICE_GAMES)
            .join(whiteAccount).onKey(SERVICE_GAMES__SERVICE_GAMES_SERVICE_ID_WHITE_PLAYER_FKEY)
            .join(blackAccount).onKey(SERVICE_GAMES__SERVICE_GAMES_SERVICE_ID_BLACK_PLAYER_FKEY)
            .where(blackAccount.USER_ID.eq(config.defaultUser)
                .or(whiteAccount.USER_ID.eq(config.defaultUser)))

        if(id.isPresent)
            query = query.and(SERVICE_GAMES.ID.eq(id.get()))

        return query.fetch { (sg, white, black) ->
            HistoryServiceGame(
                id = sg.id!!,
                // We know that the moves are not null as we verify that in the database, but
                // because it's done with a check, jooq doesn't realize and makes it nullable
                moves = sg.moves.map { Move.fromLongAlgebraicNotation(it!!) },
                startingPosition = BoardState.fromFen(sg.startingPosition),
                finalPosition = BoardState.fromFen(sg.partialFens!!.last()!!, true),
                opening = openingByGameId(sg.id!!),
                creationDate = sg.creationDate,
                result = GameResult.fromDbResult(sg.result),
                metadata = sg.metadata?.data()?.let { json -> Json.decodeFromString<Map<String, String>>(json) }
                    ?: emptyMap(),
                gameIdInService = sg.gameIdInService,
                service = Service.fromId(sg.serviceId),
                blackPlayer = ServiceAccount(
                    Service.fromId(black.serviceId),
                    black.userIdInService,
                    black.displayName,
                    black.isBot,
                    black.userId == config.defaultUser
                ),
                whitePlayer = ServiceAccount(
                    Service.fromId(white.serviceId),
                    white.userIdInService,
                    white.displayName,
                    white.isBot,
                    white.userId == config.defaultUser
                )
            )
        }
    }

    private fun pgnGamesRequest(id: Optional<Int>): List<PgnGame> {
        var query = dsl.selectFrom(PGN_GAMES)
            .where(PGN_GAMES.OWNER_ID.eq(config.defaultUser))

        if(id.isPresent)
            query = query.and(PGN_GAMES.ID.eq(id.get()))

        return query.fetch().map { resultRow ->
            PgnGame(
                id = resultRow.id!!,
                // We know that the moves are not null as we verify that in the database, but
                // because it's done with a check, jooq doesn't realize and makes it nullable
                moves = resultRow.moves.map { Move.fromLongAlgebraicNotation(it!!) },
                startingPosition = BoardState.fromFen(resultRow.startingPosition),
                finalPosition = BoardState.fromFen(resultRow.partialFens!!.last()!!, true),
                opening = openingByGameId(resultRow.id!!),
                creationDate = resultRow.creationDate,
                result = GameResult.fromDbResult(resultRow.result),
                metadata = resultRow.metadata?.data()?.let { Json.Default.decodeFromString<Map<String, String>>(it) }
                    ?: emptyMap(),
                blackPlayerName = resultRow.blackPlayerName,
                whitePlayerName = resultRow.whitePlayerName
            )
        }
    }

    // TODO: implement, this is a placeholder.
    //  The proper solution will use a database join.
    private fun openingByGameId(gameId: Int): Opening {
        return Opening(
            name = "Bishop's opening",
            eco = "C23",
            position = BoardState.fromFen("rnbqkbnr/pppp1ppp/8/4p3/2B1P3/8/PPPP1PPP/RNBQK1NR b KQkq - 1 2")
        )
    }

    override suspend fun saveGame(
        game: GameState,
        blackPlayerId: String,
        whitePlayerId: String
    ): HistoryServiceGame {
        require(game.progress is GameProgress.Finished) { "The game is not finished" }

        val id = dsl.insertInto(SERVICE_GAMES)
            .set(SERVICE_GAMES.MOVES, game.moves.map { it.toLongAlgebraicNotation() }.toTypedArray())
            .set(SERVICE_GAMES.STARTING_POSITION, game.initialState.toFenString())
            .set(SERVICE_GAMES.CREATION_DATE, LocalDateTime.now())
            .set(SERVICE_GAMES.RESULT, game.progress.result.toDbResult())
            .set(SERVICE_GAMES.IS_RANKED, true)
            .set(SERVICE_GAMES.SERVICE_ID, Service.RANDOM_CHESS.id)
            .set(SERVICE_GAMES.BLACK_PLAYER, blackPlayerId)
            .set(SERVICE_GAMES.WHITE_PLAYER, whitePlayerId)
            .returningResult(SERVICE_GAMES.ID)
            .fetchOne()?.getValue(SERVICE_GAMES.ID)
            ?: throw IllegalStateException("Failed to save game to the database")

        return serviceGamesRequest(Optional.of(id))
            .firstOrNull() ?: throw IllegalStateException("The inserted ")
    }
}
