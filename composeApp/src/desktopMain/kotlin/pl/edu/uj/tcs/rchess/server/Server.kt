package pl.edu.uj.tcs.rchess.server

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.json.Json
import org.jooq.JSONB
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.api.entity.*
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryGame
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryServiceGame
import pl.edu.uj.tcs.rchess.api.entity.game.LiveGame
import pl.edu.uj.tcs.rchess.api.entity.game.PgnGame
import pl.edu.uj.tcs.rchess.config.BotType
import pl.edu.uj.tcs.rchess.config.Config
import pl.edu.uj.tcs.rchess.generated.db.keys.SERVICE_GAMES__SERVICE_GAMES_SERVICE_ID_BLACK_PLAYER_FKEY
import pl.edu.uj.tcs.rchess.generated.db.keys.SERVICE_GAMES__SERVICE_GAMES_SERVICE_ID_WHITE_PLAYER_FKEY
import pl.edu.uj.tcs.rchess.generated.db.tables.references.PGN_GAMES
import pl.edu.uj.tcs.rchess.generated.db.tables.references.RANKINGS
import pl.edu.uj.tcs.rchess.generated.db.tables.references.SERVICE_ACCOUNTS
import pl.edu.uj.tcs.rchess.generated.db.tables.references.SERVICE_GAMES
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.Fen.Companion.fromFen
import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString
import pl.edu.uj.tcs.rchess.model.Pgn
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.state.BoardState
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import pl.edu.uj.tcs.rchess.model.state.GameState
import pl.edu.uj.tcs.rchess.server.Serialization.toDbType
import pl.edu.uj.tcs.rchess.server.Serialization.toModel
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
    override val databaseState = MutableStateFlow(
        ClientApi.DatabaseState(
            updatesAvailable = false,
            synchronizing = false
        )
    )

    init {
        val botServiceAccounts = dsl.selectFrom(SERVICE_ACCOUNTS)
            .where(SERVICE_ACCOUNTS.IS_BOT.eq(true))
            .and(SERVICE_ACCOUNTS.SERVICE_ID.eq(Service.RANDOM_CHESS.id))
            .fetch()

        botOpponents = config.bots
            .associateWith { botType ->
                botServiceAccounts.singleOrNull { sa -> sa.userIdInService == botType.serviceAccountId }
                    ?: throw IllegalStateException(
                        "Bot with id ${botType.serviceAccountId} does not exist in the database"
                    )
            }
            .map { (botType, serviceAccountRecord) ->
                BotOpponent(
                    serviceAccountRecord.displayName,
                    botType.description,
                    botType.elo,
                    serviceAccountRecord.userIdInService,
                ) to botType
            }.associate { it }

        requestResyncImpl()
    }


    override suspend fun getUserGames(refreshAvailableUpdates: Boolean): List<HistoryGame> =
        (serviceGamesRequest(Optional.empty()) + pgnGamesRequest(Optional.empty()))
            .sortedByDescending { it.creationDate }

    override suspend fun getServiceGame(id: Int): HistoryServiceGame =
        serviceGamesRequest(Optional.of(id)).singleOrNull() ?: throw IllegalArgumentException(
            "Game with id $id does not exist or is owned by another user"
        )

    override suspend fun getPgnGame(id: Int): PgnGame =
        pgnGamesRequest(Optional.of(id)).singleOrNull() ?: throw IllegalArgumentException(
            "Game with id $id does not exist or is owned by another user"
        )

    override suspend fun addPgnGames(fullPgn: String): List<Int> {
        val result = mutableListOf<Int>()
        val pgnList = Pgn.fromPgnDatabase(fullPgn)
        dsl.transaction { transaction ->
            for (pgn in pgnList) {
                result.add(
                    transaction.dsl().insertInto(PGN_GAMES)
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
            .fetchOne()?.let {
                ServiceAccount(
                    Service.RANDOM_CHESS,
                    it.userIdInService,
                    it.displayName,
                    it.isBot,
                    true
                )
            } ?: throw IllegalStateException("The system account does not exist")
    }

    override suspend fun getBotOpponents(): List<BotOpponent> =
        botOpponents.keys.toList().sortedBy { it.elo }

    override suspend fun startGameWithBot(
        playerColor: PlayerColor?,
        botOpponent: BotOpponent,
        clockSettings: ClockSettings,
        isRanked: Boolean,
    ): LiveGame {
        val finalPlayerColor = playerColor ?: listOf(PlayerColor.WHITE, PlayerColor.BLACK).random()

        val controls = botGameFactory.createAndStart(
            finalPlayerColor,
            playerServiceAccountId = config.defaultUser.toString(),
            botType = botOpponents[botOpponent]
                ?: throw IllegalArgumentException("The provided bot opponent does not exist"),
            clockSettings = clockSettings,
            isRanked = isRanked,
            coroutineScope = MainScope()
        )

        return LiveGame(
            controls = controls,
            clockSettings = clockSettings,
        )
    }

    override suspend fun getRankings(): List<Ranking> {
        return dsl
            .selectFrom(RANKINGS)
            .orderBy(
                RANKINGS.INCLUDE_BOTS.asc(),
                RANKINGS.PLAYTIME_MIN.asc(),
                RANKINGS.PLAYTIME_MAX.asc().nullsFirst(),
            )
            .fetch { it.toModel() }
    }

    override suspend fun requestResync() {
        requestResyncImpl()
    }

    private fun requestResyncImpl() {
        syncMutex.tryWithLock {
            // TODO: Implement
        }
    }

    private fun serviceGamesRequest(id: Optional<Int>): List<HistoryServiceGame> {
        val whiteAccount = SERVICE_ACCOUNTS.`as`("white_account")
        val blackAccount = SERVICE_ACCOUNTS.`as`("black_account")

        var query = dsl.select(SERVICE_GAMES, whiteAccount, blackAccount)
            .from(SERVICE_GAMES)
            .join(whiteAccount).onKey(SERVICE_GAMES__SERVICE_GAMES_SERVICE_ID_WHITE_PLAYER_FKEY)
            .join(blackAccount).onKey(SERVICE_GAMES__SERVICE_GAMES_SERVICE_ID_BLACK_PLAYER_FKEY)
            .where(
                blackAccount.USER_ID.eq(config.defaultUser)
                    .or(whiteAccount.USER_ID.eq(config.defaultUser))
            )

        if (id.isPresent)
            query = query.and(SERVICE_GAMES.ID.eq(id.get()))

        return query.fetch { (serviceGame, white, black) ->
            serviceGame.toModel(
                white = white.toModel(
                    isCurrentUser = white.userId == config.defaultUser
                ),
                black = black.toModel(
                    isCurrentUser = black.userId == config.defaultUser
                ),
                opening = openingByGameId(serviceGame.id!!),
            )
        }
    }

    private fun pgnGamesRequest(id: Optional<Int>): List<PgnGame> {
        var query = dsl.selectFrom(PGN_GAMES)
            .where(PGN_GAMES.OWNER_ID.eq(config.defaultUser))

        if (id.isPresent)
            query = query.and(PGN_GAMES.ID.eq(id.get()))

        return query.fetch().map { resultRow ->
            resultRow.toModel(
                opening = openingByGameId(resultRow.id!!),
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
        whitePlayerId: String,
        isRanked: Boolean,
        clockSettings: ClockSettings,
    ): HistoryServiceGame {
        require(game.progress is GameProgress.Finished) { "The game is not finished" }

        val id = dsl.insertInto(SERVICE_GAMES)
            .set(SERVICE_GAMES.MOVES, game.moves.map { it.toLongAlgebraicNotation() }.toTypedArray())
            .set(SERVICE_GAMES.STARTING_POSITION, game.initialState.toFenString())
            .set(SERVICE_GAMES.CREATION_DATE, LocalDateTime.now())
            .set(SERVICE_GAMES.RESULT, game.progress.result.toDbResult())
            .set(SERVICE_GAMES.IS_RANKED, isRanked)
            .set(SERVICE_GAMES.SERVICE_ID, Service.RANDOM_CHESS.id)
            .set(SERVICE_GAMES.BLACK_PLAYER, blackPlayerId)
            .set(SERVICE_GAMES.WHITE_PLAYER, whitePlayerId)
            .set(SERVICE_GAMES.CLOCK, clockSettings.toDbType())
            .returningResult(SERVICE_GAMES.ID)
            .fetchOne()?.getValue(SERVICE_GAMES.ID)
            ?: throw IllegalStateException("Failed to save game to the database")

        return serviceGamesRequest(Optional.of(id))
            .singleOrNull()
            ?: throw IllegalStateException("The game that was just inserted does not exist in the database")
    }
}
