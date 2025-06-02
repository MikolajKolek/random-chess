package pl.edu.uj.tcs.rchess.server

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.json.Json
import org.jooq.JSONB
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.kotlin.coroutines.transactionCoroutine
import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.api.entity.BotOpponent
import pl.edu.uj.tcs.rchess.api.entity.Ranking
import pl.edu.uj.tcs.rchess.api.entity.Service
import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryGame
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryServiceGame
import pl.edu.uj.tcs.rchess.api.entity.game.LiveGame
import pl.edu.uj.tcs.rchess.api.entity.game.PgnGame
import pl.edu.uj.tcs.rchess.config.BotType
import pl.edu.uj.tcs.rchess.config.ConfigLoader
import pl.edu.uj.tcs.rchess.generated.db.keys.SERVICE_GAMES__SERVICE_GAMES_SERVICE_ID_BLACK_PLAYER_FKEY
import pl.edu.uj.tcs.rchess.generated.db.keys.SERVICE_GAMES__SERVICE_GAMES_SERVICE_ID_WHITE_PLAYER_FKEY
import pl.edu.uj.tcs.rchess.generated.db.tables.references.*
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString
import pl.edu.uj.tcs.rchess.model.Pgn
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import pl.edu.uj.tcs.rchess.model.state.GameState
import pl.edu.uj.tcs.rchess.server.Serialization.toDbResult
import pl.edu.uj.tcs.rchess.server.Serialization.toDbType
import pl.edu.uj.tcs.rchess.server.Serialization.toModel
import pl.edu.uj.tcs.rchess.util.tryWithLock
import reactor.core.publisher.Flux
import java.time.LocalDateTime
import java.util.*

class Server() : ClientApi, Database {
    private val config = ConfigLoader.loadConfig()
    private val connection = ConnectionFactories.get(
        ConnectionFactoryOptions
            .parse("r2dbc:postgresql://${config.database.host}:${config.database.port}/${config.database.database}")
            .mutate()
            .option(ConnectionFactoryOptions.USER, config.database.user)
            .option(ConnectionFactoryOptions.PASSWORD, config.database.password)
            .build()
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
        val botServiceAccounts = runBlocking {
            Flux.from(dsl.selectFrom(SERVICE_ACCOUNTS)
                .where(SERVICE_ACCOUNTS.IS_BOT.eq(true))
                .and(SERVICE_ACCOUNTS.SERVICE_ID.eq(Service.RANDOM_CHESS.id))
            ).asFlow().toList()
        }

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
        dsl.transactionCoroutine { transaction ->
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
                        .awaitFirst()?.getValue(PGN_GAMES.ID)
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
            .awaitFirst()?.let {
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
        return Flux.from(
            dsl.selectFrom(RANKINGS)
                .orderBy(
                    RANKINGS.INCLUDE_BOTS.asc(),
                    RANKINGS.PLAYTIME_MIN.asc(),
                    RANKINGS.PLAYTIME_MAX.asc().nullsFirst(),
                )
        ).asFlow().map { it.toModel() }.toList()
    }

    override suspend fun requestResync() {
        requestResyncImpl()
    }

    private fun requestResyncImpl() {
        syncMutex.tryWithLock {
            // TODO: Implement
        }
    }

    private suspend fun serviceGamesRequest(id: Optional<Int>): List<HistoryServiceGame> {
        val whiteAccount = SERVICE_ACCOUNTS.`as`("white_account")
        val blackAccount = SERVICE_ACCOUNTS.`as`("black_account")

        var query = dsl.select(
                SERVICE_GAMES,
                whiteAccount,
                blackAccount,
                GAMES_OPENINGS,
                OPENINGS
            ).from(SERVICE_GAMES)
            .join(whiteAccount).onKey(SERVICE_GAMES__SERVICE_GAMES_SERVICE_ID_WHITE_PLAYER_FKEY)
            .join(blackAccount).onKey(SERVICE_GAMES__SERVICE_GAMES_SERVICE_ID_BLACK_PLAYER_FKEY)
            .join(GAMES_OPENINGS).on(
                SERVICE_GAMES.ID.eq(GAMES_OPENINGS.GAME_ID)
                    .and(GAMES_OPENINGS.KIND.eq("service"))
            )
            .leftJoin(OPENINGS).on(GAMES_OPENINGS.OPENING_ID.eq(OPENINGS.ID))
            .where(
                blackAccount.USER_ID.eq(config.defaultUser)
                    .or(whiteAccount.USER_ID.eq(config.defaultUser))
            )

        if (id.isPresent)
            query = query.and(SERVICE_GAMES.ID.eq(id.get()))

        return Flux.from(query).asFlow().map { (serviceGame, white, black, _, opening) ->
            serviceGame.toModel(
                white = white.toModel(
                    isCurrentUser = white.userId == config.defaultUser
                ),
                black = black.toModel(
                    isCurrentUser = black.userId == config.defaultUser
                ),
                opening = opening.toModel()
            )
        }.toList()
    }

    private suspend fun pgnGamesRequest(id: Optional<Int>): List<PgnGame> {
        var query = dsl.select(PGN_GAMES, GAMES_OPENINGS, OPENINGS)
            .from(PGN_GAMES)
            .join(GAMES_OPENINGS).on(
                PGN_GAMES.ID.eq(GAMES_OPENINGS.GAME_ID)
                    .and(GAMES_OPENINGS.KIND.eq("pgn"))
            )
            .leftJoin(OPENINGS).on(GAMES_OPENINGS.OPENING_ID.eq(OPENINGS.ID))
            .where(PGN_GAMES.OWNER_ID.eq(config.defaultUser))

        if (id.isPresent)
            query = query.and(PGN_GAMES.ID.eq(id.get()))

        return Flux.from(query).asFlow().map { (pgnGame, _, opening) ->
            pgnGame.toModel(
                opening = opening.toModel()
            )
        }.toList()
    }

    override suspend fun saveGame(
        game: GameState,
        blackPlayerId: String,
        whitePlayerId: String,
        isRanked: Boolean,
        clockSettings: ClockSettings,
    ): HistoryServiceGame {
        val progress = game.progress
        require(progress is GameProgress.Finished) { "The game is not finished" }

        val id = dsl.insertInto(SERVICE_GAMES)
            .set(SERVICE_GAMES.MOVES, game.moves.map { it.toLongAlgebraicNotation() }.toTypedArray())
            .set(SERVICE_GAMES.STARTING_POSITION, game.initialState.toFenString())
            .set(SERVICE_GAMES.CREATION_DATE, LocalDateTime.now())
            .set(SERVICE_GAMES.RESULT, progress.result.toDbResult())
            .set(SERVICE_GAMES.IS_RANKED, isRanked)
            .set(SERVICE_GAMES.SERVICE_ID, Service.RANDOM_CHESS.id)
            .set(SERVICE_GAMES.BLACK_PLAYER, blackPlayerId)
            .set(SERVICE_GAMES.WHITE_PLAYER, whitePlayerId)
            .set(SERVICE_GAMES.CLOCK, clockSettings.toDbType())
            .returningResult(SERVICE_GAMES.ID)
            .awaitFirst()?.getValue(SERVICE_GAMES.ID)
            ?: throw IllegalStateException("Failed to save game to the database")

        return serviceGamesRequest(Optional.of(id))
            .singleOrNull()
            ?: throw IllegalStateException("The game that was just inserted does not exist in the database")
    }
}
