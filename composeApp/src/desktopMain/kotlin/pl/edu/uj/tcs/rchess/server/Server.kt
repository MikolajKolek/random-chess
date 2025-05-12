package pl.edu.uj.tcs.rchess.server

import kotlinx.serialization.json.Json
import org.jooq.JSONB
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import pl.edu.uj.tcs.rchess.db.keys.SERVICE_GAMES__SERVICE_GAMES_SERVICE_ID_BLACK_PLAYER_FKEY
import pl.edu.uj.tcs.rchess.db.keys.SERVICE_GAMES__SERVICE_GAMES_SERVICE_ID_WHITE_PLAYER_FKEY
import pl.edu.uj.tcs.rchess.db.tables.references.PGN_GAMES
import pl.edu.uj.tcs.rchess.db.tables.references.SERVICE_ACCOUNTS
import pl.edu.uj.tcs.rchess.db.tables.references.SERVICE_GAMES
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Fen.Companion.fromFen
import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.Pgn
import pl.edu.uj.tcs.rchess.viewmodel.Config
import java.sql.DriverManager
import java.time.LocalDateTime
import java.util.*

class Server(private val config: Config) : ClientApi {
    private val connection = DriverManager.getConnection(
        "jdbc:postgresql://${config.database.host}:${config.database.port}/${config.database.database}",
        config.database.user,
        config.database.password
    )
    private val dsl = DSL.using(connection, SQLDialect.POSTGRES)
    private val botOpponents: List<BotOpponent>

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
                serviceAccountRecord.userIdInService,
            ) }
    }


    override suspend fun getUserGames(): List<HistoryGame> {
        return serviceGamesRequest(Optional.empty()) + pgnGamesRequest(Optional.empty())
    }

    override suspend fun getServiceGame(id: Int): ServiceGame {
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
        dsl.transaction { transaction ->
            for(pgn in Pgn.fromPgnDatabase(fullPgn)) {
                result.add(transaction.dsl().insertInto(PGN_GAMES)
                    .set(PGN_GAMES.MOVES, pgn.moves.map { it.toLongAlgebraicNotation() }.toTypedArray())
                    .set(PGN_GAMES.STARTING_POSITION, pgn.startingPosition.toFenString())
                    .set(PGN_GAMES.CREATION_DATE, LocalDateTime.now())
                    .set(PGN_GAMES.RESULT, pgn.result.dbResult)
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
            }!!
    }

    override suspend fun getBotOpponents(): List<BotOpponent> {
        return botOpponents
    }


    private fun serviceGamesRequest(id: Optional<Int>): List<ServiceGame> {
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
            ServiceGame(
                id = sg.id!!,
                startingPosition = BoardState.fromFen(sg.startingPosition),
                moves = sg.moves.map { Move.fromLongAlgebraicNotation(it!!) },
                creationDate = sg.creationDate,
                result = GameResult.fromDbResult(sg.result),
                metadata = sg.metadata?.data()?.let { json -> Json.decodeFromString(json) },
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

        return query.fetch().map { PgnGame(it) }
    }
}
