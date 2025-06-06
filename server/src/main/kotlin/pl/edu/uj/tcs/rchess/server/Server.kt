package pl.edu.uj.tcs.rchess.server

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.json.Json
import org.jooq.Condition
import org.jooq.JSONB
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.DSL.*
import org.jooq.impl.SQLDataType.INTEGER
import org.jooq.kotlin.coroutines.transactionCoroutine
import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.api.entity.BotOpponent
import pl.edu.uj.tcs.rchess.api.entity.Service
import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.api.entity.game.*
import pl.edu.uj.tcs.rchess.api.entity.ranking.Ranking
import pl.edu.uj.tcs.rchess.api.entity.ranking.RankingSpot
import pl.edu.uj.tcs.rchess.config.BotType
import pl.edu.uj.tcs.rchess.config.ConfigLoader
import pl.edu.uj.tcs.rchess.generated.db.tables.references.*
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString
import pl.edu.uj.tcs.rchess.model.Pgn
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import pl.edu.uj.tcs.rchess.model.state.GameState
import pl.edu.uj.tcs.rchess.server.Serialization.toDbId
import pl.edu.uj.tcs.rchess.server.Serialization.toDbResult
import pl.edu.uj.tcs.rchess.server.Serialization.toDbType
import pl.edu.uj.tcs.rchess.server.Serialization.toModel
import pl.edu.uj.tcs.rchess.util.tryWithLock
import reactor.core.publisher.Flux
import java.time.LocalDateTime

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
    private val dsl = using(connection, SQLDialect.POSTGRES)
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
                .and(SERVICE_ACCOUNTS.SERVICE_ID.eq(Service.RANDOM_CHESS.toDbId()))
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

    override suspend fun getUserGames(settings: ClientApi.GamesRequestSettings): List<HistoryGame> =
        modifiableUserGamesRequest(settings)

    override suspend fun getServiceGame(id: Int): HistoryServiceGame =
        modifiableUserGamesRequest(
            settings = ClientApi.GamesRequestSettings(
                includePgnGames = false,
                length = 1
            ),
            extraConditions = GAMES.ID.eq(id)
        ).first() as? HistoryServiceGame
            ?: throw IllegalArgumentException("Game with id $id does not exist or is owned by another user")

    override suspend fun getPgnGame(id: Int): PgnGame =
        modifiableUserGamesRequest(
            settings = ClientApi.GamesRequestSettings(
                includedServices = setOf(),
                length = 1
            ),
            extraConditions = GAMES.ID.eq(id)
        ).first() as? PgnGame
            ?: throw IllegalArgumentException("Game with id $id does not exist or is owned by another user")

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

        databaseState.getAndUpdate {
            it.copy(updatesAvailable = true)
        }

        return result
    }

    override suspend fun getSystemAccount(): ServiceAccount =
        serviceAccountById(config.defaultUser.toString(), Service.RANDOM_CHESS)

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
            whitePlayer =
                if(finalPlayerColor == PlayerColor.WHITE) getSystemAccount()
                else serviceAccountById(botOpponent.serviceAccountId, Service.RANDOM_CHESS),
            blackPlayer =
                if(finalPlayerColor == PlayerColor.BLACK) getSystemAccount()
                else serviceAccountById(botOpponent.serviceAccountId, Service.RANDOM_CHESS)
        )
    }

    override suspend fun getRankingsList(): List<Ranking> {
        return Flux.from(
            dsl.selectFrom(RANKINGS)
                .orderBy(
                    RANKINGS.INCLUDE_BOTS.asc(),
                    RANKINGS.PLAYTIME_MIN.asc(),
                    RANKINGS.PLAYTIME_MAX.asc().nullsFirst(),
                )
        ).asFlow().map { it.toModel() }.toList()
    }

    override suspend fun getRankingPlacements(settings: ClientApi.RankingRequestSettings): List<RankingSpot> {
        var conditions = noCondition()

        settings.after?.let {
            conditions = RANKING_WITH_PLACEMENT_AT_TIMESTAMP.PLACEMENT.greaterThan(it.placement).or(
                RANKING_WITH_PLACEMENT_AT_TIMESTAMP.PLACEMENT.eq(it.placement).and(
                    SERVICE_ACCOUNTS.DISPLAY_NAME.greaterThan(it.serviceAccount.displayName).or(
                        SERVICE_ACCOUNTS.DISPLAY_NAME.eq(it.serviceAccount.displayName).and(
                            SERVICE_ACCOUNTS.USER_ID_IN_SERVICE
                                .greaterThan(it.serviceAccount.userIdInService)
                        )
                    )
                )
            )
        }

        val query = dsl.select(RANKING_WITH_PLACEMENT_AT_TIMESTAMP, SERVICE_ACCOUNTS)
            .from(
                RANKING_WITH_PLACEMENT_AT_TIMESTAMP(
                    settings.atTimestamp,
                    settings.ranking.id
                )
            )
            .join(SERVICE_ACCOUNTS).on(
                RANKING_WITH_PLACEMENT_AT_TIMESTAMP.SERVICE_ID
                    .eq(SERVICE_ACCOUNTS.SERVICE_ID)
                    .and(
                        RANKING_WITH_PLACEMENT_AT_TIMESTAMP.USER_ID_IN_SERVICE
                            .eq(SERVICE_ACCOUNTS.USER_ID_IN_SERVICE)
                    )
            )
            .where(conditions)
            .orderBy(
                RANKING_WITH_PLACEMENT_AT_TIMESTAMP.PLACEMENT.asc(),
                SERVICE_ACCOUNTS.DISPLAY_NAME.asc(),
                SERVICE_ACCOUNTS.USER_ID_IN_SERVICE.asc()
            )
            .limit(settings.length)

        return Flux.from(query).asFlow().map { (entry, account) ->
            entry.toModel(
                serviceAccount = account.toModel(isCurrentUser = account.userId == config.defaultUser)
            )
        }.toList()
    }

    override suspend fun requestResync() {
        requestResyncImpl()
    }

    private fun requestResyncImpl() {
        syncMutex.tryWithLock {
            // TODO: Implement
        }
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
            .set(SERVICE_GAMES.SERVICE_ID, Service.RANDOM_CHESS.toDbId())
            .set(SERVICE_GAMES.BLACK_PLAYER, blackPlayerId)
            .set(SERVICE_GAMES.WHITE_PLAYER, whitePlayerId)
            .set(SERVICE_GAMES.CLOCK, clockSettings.toDbType())
            .returningResult(SERVICE_GAMES.ID)
            .awaitFirst()?.getValue(SERVICE_GAMES.ID)
            ?: throw IllegalStateException("Failed to save game to the database")

        databaseState.getAndUpdate {
            it.copy(updatesAvailable = true)
        }

        return getServiceGame(id)
    }

    //TODO: this is pretty horrible and probably can be done better
    private suspend fun modifiableUserGamesRequest(
        settings: ClientApi.GamesRequestSettings,
        extraConditions: Condition = noCondition()
    ): List<HistoryGame> {
        val currentEloHistory = ELO_HISTORY.`as`("current_elo_history")
        val previousEloHistory = ELO_HISTORY.`as`("previous_elo_history")
        val whiteAccount = SERVICE_ACCOUNTS.`as`("white_account")
        val blackAccount = SERVICE_ACCOUNTS.`as`("black_account")

        //TODO: eq, or, etc. can probably be made nicer as infix operators
        var conditions = extraConditions.and(
            (blackAccount.USER_ID.eq(config.defaultUser)
            .or(whiteAccount.USER_ID.eq(config.defaultUser))
            .or(GAMES.PGN_OWNER_ID.eq(config.defaultUser)))
        )

        if(!settings.includePgnGames)
            conditions = conditions.and(GAMES.KIND.notEqual("pgn"))

        settings.includedServices?.let { includedServices ->
            var serviceCondition = noCondition()
            for (service in includedServices) {
                serviceCondition = serviceCondition.or(
                    GAMES.SERVICE_ID.eq(service.toDbId())
                )
            }
            conditions = conditions.and(serviceCondition)
        }

        settings.after?.let {
            conditions = conditions.and(
                GAMES.CREATION_DATE.lessThan(it.creationDate).or(
                    GAMES.CREATION_DATE.eq(it.creationDate).and(
                        GAMES.ID.lessThan(it.id).or(
                            GAMES.ID.eq(it.id).and(
                                GAMES.KIND.lessThan(when(it) {
                                    is ServiceGame -> "service"
                                    is PgnGame -> "pgn"
                                })
                            )
                        )
                    )
                )
            )
        }

        val query = dsl.select(
            GAMES,
            whiteAccount,
            blackAccount,
            GAMES_OPENINGS,
            OPENINGS,
            multiset(
                select(
                    currentEloHistory.ELO.cast(INTEGER),
                    coalesce(previousEloHistory.ELO, RANKINGS.STARTING_ELO).cast(INTEGER),
                    currentEloHistory.USER_ID_IN_SERVICE,
                    RANKINGS
                )
                    .from(currentEloHistory)
                    .leftJoin(previousEloHistory).on(
                        currentEloHistory.PREVIOUS_ENTRY.eq(previousEloHistory.ID)
                    )
                    .join(RANKINGS).on(
                        currentEloHistory.RANKING_ID.eq(RANKINGS.ID)
                    )
                    .where(
                        currentEloHistory.GAME_ID.eq(GAMES.ID)
                            .and(currentEloHistory.SERVICE_ID.eq(GAMES.SERVICE_ID))
                    )
            )
        ).from(GAMES)
            .leftJoin(whiteAccount).on(
                GAMES.WHITE_SERVICE_ACCOUNT.eq(whiteAccount.USER_ID_IN_SERVICE)
                    .and(GAMES.SERVICE_ID.eq(whiteAccount.SERVICE_ID))
            )
            .leftJoin(blackAccount).on(
                GAMES.BLACK_SERVICE_ACCOUNT.eq(blackAccount.USER_ID_IN_SERVICE)
                    .and(GAMES.SERVICE_ID.eq(blackAccount.SERVICE_ID))
            )
            .join(GAMES_OPENINGS).on(
                GAMES.ID.eq(GAMES_OPENINGS.GAME_ID)
                    .and(GAMES_OPENINGS.KIND.eq(GAMES.KIND))
            )
            .leftJoin(OPENINGS).on(GAMES_OPENINGS.OPENING_ID.eq(OPENINGS.ID))
            .where(conditions)
            .orderBy(GAMES.CREATION_DATE.desc(), GAMES.ID.desc(), GAMES.KIND.desc())
            .limit(settings.length)

        val result = Flux.from(query).asFlow().map { (game, white, black, _, opening, rankingUpdates) ->
            game.toModel(
                white = white,
                black = black,
                currentUserId = config.defaultUser,
                opening = opening.toModel(),
                rankingUpdates = rankingUpdates
            )
        }.toList()

        // This should only happen when we know the function is not going to throw
        if(settings.refreshAvailableUpdates)
            databaseState.getAndUpdate { it.copy(updatesAvailable = false) }

        return result
    }

    private suspend fun serviceAccountById(userId: String, service: Service): ServiceAccount {
        return dsl.selectFrom(SERVICE_ACCOUNTS)
            .where(SERVICE_ACCOUNTS.USER_ID_IN_SERVICE.eq(userId))
            .and(SERVICE_ACCOUNTS.SERVICE_ID.eq(service.toDbId()))
            .awaitFirst()?.let {
                ServiceAccount(
                    service,
                    it.userIdInService,
                    it.displayName,
                    it.isBot,
                    (userId == config.defaultUser.toString())
                            && (service == Service.RANDOM_CHESS)
                )
            } ?: throw IllegalStateException("The requested service account does not exist")
    }
}
