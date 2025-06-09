package pl.edu.uj.tcs.rchess.external.lichess

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.chunked
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.timeout
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import pl.edu.uj.tcs.rchess.UnsavedServiceGame
import pl.edu.uj.tcs.rchess.api.entity.Service
import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.external.ExternalConnection
import pl.edu.uj.tcs.rchess.model.*
import pl.edu.uj.tcs.rchess.server.Database
import pl.edu.uj.tcs.rchess.util.logger
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

internal class LichessConnection(
    override val database: Database,
    override val serviceAccount: ServiceAccount
) : ExternalConnection {
    private val httpClient = HttpClient(CIO)
    private var lastRequest: Instant? = null

    override fun available(): Boolean {
        val currentLastRequest = lastRequest
        return (currentLastRequest == null || Clock.System.now() - currentLastRequest >= 5.seconds)
    }

    override suspend fun synchronize(): Boolean {
        if (!available())
            return true
        lastRequest = Clock.System.now()

        try {
            synchronizeImpl()
        } catch (e: Throwable) {
            logger.error { "Lichess synchronization failed: ${e.message}" }
            return false
        }

        return true
    }

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class, FlowPreview::class)
    suspend fun synchronizeImpl() = coroutineScope {
        val token = database.getTokenForServiceAccount(serviceAccount)
        val latestGameEpochMillisecond = database
            .getLatestGameForServiceAccount(serviceAccount)
            ?.creationDate?.toInstant()?.toEpochMilli()

        val request = httpClient.prepareGet(
            lichessApiUrl + "/games/user/${serviceAccount.userIdInService}",
        ) {
            header("Accept", "application/x-ndjson")
            token?.let {
                header("Authorization", "Bearer $it")
            }

            parameter("perfType", "ultraBullet,bullet,blitz,rapid,classical,correspondence")
            parameter("pgnInJson", "true")
            parameter("sort", "dateAsc")
            latestGameEpochMillisecond?.let {
                parameter("since", it)
            }

            timeout {
                requestTimeoutMillis = null
            }
        }

        val tasks = Channel<String>(Channel.UNLIMITED)
        launch {
            request.execute { httpResponse ->
                val channel = httpResponse.bodyAsChannel()

                while (!channel.exhausted()) {
                    channel.readUTF8Line()?.let {
                        tasks.send(it)
                    }
                }

                tasks.close()
            }
        }

        tasks.receiveAsFlow().chunked(BATCH_SIZE).timeout(10.seconds).collect { chunk ->
            val processedGames = chunk.map { jsonResponse ->
                async {
                    val response = Json.decodeFromString<LichessGamesResponse>(jsonResponse)
                    val pgn = Pgn.fromPgnDatabase(response.pgn).first()

                    if (listOf("created", "started", "aborted", "variantEnd").contains(response.status)) {
                        return@async null
                    }

                    val whitePlayer = response.players["white"]?.let { player ->
                        ServiceAccount.fromLichessPlayer(player)
                    }
                    val blackPlayer = response.players["black"]?.let { player ->
                        ServiceAccount.fromLichessPlayer(player)
                    }

                    if (whitePlayer == null || blackPlayer == null)
                        return@async null

                    UnsavedServiceGame(
                        moves = pgn.moves,
                        startingPosition = pgn.startingPosition,
                        creationDate = response.createdAt,
                        result = if (
                            (pgn.result as? Win)?.winReason == GameWinReason.UNKNOWN ||
                            (pgn.result as? Draw)?.drawReason == GameDrawReason.UNKNOWN
                        ) GameResult.fromLichessStatus(
                            lichessStatus = response.status,
                            winner = response.winner
                        ) else pgn.result,
                        metadata = pgn.metadata?.jsonObject?.let { metadataJson ->
                            Json.Default.decodeFromJsonElement<Map<String, String>>(metadataJson)
                        } ?: emptyMap(),
                        gameIdInService = response.id,
                        service = Service.LICHESS,
                        blackPlayer = blackPlayer,
                        whitePlayer = whitePlayer,
                        clockSettings = response.clock?.let { clock ->
                            ClockSettings.fromLichessClock(clock)
                        }
                    )
                }
            }

            database.saveServiceGames(processedGames.awaitAll().filterNotNull())
        }
    }

    companion object {
        private const val BATCH_SIZE = 60
    }
}

fun GameResult.Companion.fromLichessStatus(lichessStatus: String, winner: String?): GameResult {
    val winColor = winner?.let {
        when (it) {
            "black" -> PlayerColor.BLACK
            "white" -> PlayerColor.WHITE
            else -> null
        }
    }

    return when (lichessStatus) {
        "mate" -> Win(GameWinReason.CHECKMATE, winColor!!)
        "draw" -> Draw(GameDrawReason.UNKNOWN)
        "stalemate" -> Draw(GameDrawReason.STALEMATE)
        "outoftime", "timeout" -> winColor?.let { Win(GameWinReason.TIMEOUT, it) }
            ?: Draw(GameDrawReason.TIMEOUT_VS_INSUFFICIENT_MATERIAL)

        "resign" -> Win(GameWinReason.RESIGNATION, winColor!!)
        else -> winColor?.let { Win(GameWinReason.UNKNOWN, it) }
            ?: Draw(GameDrawReason.UNKNOWN)
    }
}

fun ServiceAccount.Companion.fromLichessPlayer(lichessPlayer: LichessPlayer): ServiceAccount? {
    return when (lichessPlayer) {
        is EmptyLichessPlayer -> null
        is LichessAccount -> ServiceAccount(
            service = Service.LICHESS,
            userIdInService = lichessPlayer.user.id,
            displayName = lichessPlayer.user.name,
            isBot = lichessPlayer.user.title == "BOT",
            isCurrentUser = false,
        )

        is LichessBot -> ServiceAccount(
            service = Service.LICHESS,
            // TODO: this probably also shouldn't be hardcoded
            userIdInService = lichessPlayer.aiLevel.toString(),
            displayName = "Lichess bot (Level ${lichessPlayer.aiLevel})",
            isBot = true,
            isCurrentUser = false,
        )
    }
}

fun ClockSettings.Companion.fromLichessClock(clock: LichessClock) = ClockSettings(
    startingTime = clock.initial.seconds,
    moveIncrease = clock.increment.seconds,
    extraTimeForFirstMove = 0.seconds
)