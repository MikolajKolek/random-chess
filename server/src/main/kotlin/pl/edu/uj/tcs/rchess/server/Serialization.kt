package pl.edu.uj.tcs.rchess.server

import kotlinx.serialization.json.Json
import org.jooq.Record4
import org.jooq.types.YearToSecond
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.api.entity.Opening
import pl.edu.uj.tcs.rchess.api.entity.PlayerDetails
import pl.edu.uj.tcs.rchess.api.entity.Service
import pl.edu.uj.tcs.rchess.api.entity.Service.UNKNOWN
import pl.edu.uj.tcs.rchess.api.entity.Service.entries
import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryGame
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryServiceGame
import pl.edu.uj.tcs.rchess.api.entity.game.PgnGame
import pl.edu.uj.tcs.rchess.api.entity.ranking.EloUpdate
import pl.edu.uj.tcs.rchess.api.entity.ranking.Ranking
import pl.edu.uj.tcs.rchess.api.entity.ranking.RankingSpot
import pl.edu.uj.tcs.rchess.api.entity.ranking.RankingUpdate
import pl.edu.uj.tcs.rchess.generated.db.tables.records.*
import pl.edu.uj.tcs.rchess.generated.db.udt.records.ClockSettingsTypeRecord
import pl.edu.uj.tcs.rchess.generated.db.udt.records.GameResultTypeRecord
import pl.edu.uj.tcs.rchess.model.*
import pl.edu.uj.tcs.rchess.model.Fen.Companion.fromFen
import pl.edu.uj.tcs.rchess.model.state.BoardState
import kotlin.time.Duration
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

internal object Serialization {
    fun Duration.toDbInterval(): YearToSecond =
        YearToSecond.valueOf(this.toJavaDuration())

    fun YearToSecond.toKotlinDuration(): Duration =
        this.toDuration().toKotlinDuration()

    fun ClockSettings?.toDbType(): ClockSettingsTypeRecord {
        if (this == null) return ClockSettingsTypeRecord(
            startingTime = null,
            moveIncrease = null,
        )

        return ClockSettingsTypeRecord(
            startingTime = startingTime.toDbInterval(),
            moveIncrease = moveIncrease.toDbInterval(),
        )
    }

    fun ClockSettingsTypeRecord.toModel(): ClockSettings? {
        require((this.startingTime == null) == (this.moveIncrease == null)) {
            "Both startingTime and moveIncrease must be null or non-null"
        }
        val startingTime = this.startingTime ?: return null
        val moveIncrease = this.moveIncrease ?: return null

        return ClockSettings(
            startingTime = startingTime.toKotlinDuration(),
            moveIncrease = moveIncrease.toKotlinDuration(),
        )
    }

    fun RankingsRecord.toModel() = Ranking(
        id = id!!,
        name = name,
        playtimeMin = playtimeMin.toKotlinDuration(),
        playtimeMax = playtimeMax?.toKotlinDuration() ?: Duration.INFINITE,
        extraMoveMultiplier = extraMoveMultiplier,
        includeBots = includeBots
    )

    fun ServiceAccountsRecord.toModel(
        currentUserId: Int?,
    ) = ServiceAccount(
        service = Service.fromDbId(serviceId),
        userIdInService = userIdInService,
        displayName = displayName,
        isBot = isBot,
        isCurrentUser = userId == currentUserId,
    )

    fun GamesRecord.toModel(
        white: ServiceAccountsRecord?,
        black: ServiceAccountsRecord?,
        rankingUpdates: org.jooq.Result<Record4<Int, Int?, String?, RankingsRecord>>?,
        currentUserId: Int?,
        opening: Opening?,
    ): HistoryGame = when(kind) {
        "service" -> HistoryServiceGame(
            id = id!!,
            moves = moves!!.map { Move.fromLongAlgebraicNotation(it!!) },
            startingPosition = BoardState.fromFen(startingPosition!!),
            finalPosition = BoardState.fromFen(partialFens!!.last()!!, true),
            opening = opening,
            creationDate = creationDate!!,
            result = GameResult.fromDbResult(result!!),
            metadata = metadata?.data()?.let { Json.Default.decodeFromString<Map<String, String>>(it) }
                ?: emptyMap(),
            gameIdInService = gameIdInService,
            service = Service.fromDbId(serviceId!!),
            blackPlayer = black!!.toModel(
                currentUserId
            ),
            whitePlayer = white!!.toModel(
                currentUserId
            ),
            clockSettings = clock?.toModel(),
            rankingUpdates = rankingUpdates?.groupBy { (_, _, _, ranking) -> ranking.toModel() }
                ?.map { (ranking, updates) ->
                    RankingUpdate(
                        ranking = ranking,
                        blackEloUpdate = updates.find { (_, _, userIdInService, _) ->
                                userIdInService == black.userIdInService
                            }?.let { (currentElo, previousElo, _, _) ->
                                EloUpdate(newElo = currentElo, oldElo = previousElo!!)
                            },
                        whiteEloUpdate = updates.find { (_, _, userIdInService, _) ->
                                userIdInService == white.userIdInService
                            }?.let { (currentElo, previousElo, _, _) ->
                                EloUpdate(newElo = currentElo, oldElo = previousElo!!)
                            }
                    )
                } ?: emptyList()
        )
        "pgn" -> PgnGame(
            id = id!!,
            moves = moves!!.map { Move.fromLongAlgebraicNotation(it!!) },
            startingPosition = BoardState.fromFen(startingPosition!!),
            finalPosition = BoardState.fromFen(partialFens!!.last()!!, true),
            opening = opening,
            creationDate = creationDate!!,
            result = GameResult.fromDbResult(result!!),
            metadata = metadata?.data()?.let { Json.Default.decodeFromString<Map<String, String>>(it) }
                ?: emptyMap(),
            blackPlayer = PlayerDetails.Simple(pgnBlackPlayerName!!),
            whitePlayer = PlayerDetails.Simple(pgnWhitePlayerName!!),
            clockSettings = clock?.toModel()
        )
        else -> throw IllegalArgumentException("Invalid game kind: $kind")
    }

    fun OpeningsRecord.toModelWith(gameOpening: GamesOpeningsRecord) = id?.let {
        Opening(
            eco = eco,
            name = name,
            position = BoardState.fromFen(partialFen, true),
            moveNumber = gameOpening.moveNo!!,
        )
    }

    fun GameResult.Companion.fromDbResult(result: GameResultTypeRecord): GameResult {
        return when (result.gameEndType) {
            "1-0" -> Win(GameWinReason.fromDbString(result.gameEndReason), PlayerColor.WHITE)
            "0-1" -> Win(GameWinReason.fromDbString(result.gameEndReason), PlayerColor.BLACK)
            "1/2-1/2" -> Draw(GameDrawReason.fromDbString(result.gameEndReason))
            else -> throw IllegalArgumentException("Invalid db game_result")
        }
    }

    fun GameResult.toDbResult(): GameResultTypeRecord {
        return when (this) {
            is Win -> GameResultTypeRecord(gameEndType = toPgnString(), gameEndReason = winReason.toDbWinReason())
            is Draw -> GameResultTypeRecord(gameEndType = toPgnString(), gameEndReason = drawReason.toDbWinReason())
        }
    }

    fun Service.toDbId() = when(this) {
        UNKNOWN -> throw IllegalArgumentException("Cannot convert UNKNOWN to db id")
        Service.RANDOM_CHESS -> 1
        Service.LICHESS -> 3
    }

    fun Service.Companion.fromDbId(id: Int): Service =
        entries.filter { it != UNKNOWN }.find { it.toDbId() == id } ?: UNKNOWN

    fun RankingWithPlacementAtTimestampRecord.toModel(
        serviceAccount: ServiceAccount,
    ): RankingSpot = RankingSpot(
        placement = placement!!,
        serviceAccount = serviceAccount,
        elo = elo!!
    )

    fun GameWinReason.toDbWinReason() = when(this) {
        GameWinReason.UNKNOWN -> "UNKNOWN"
        GameWinReason.TIMEOUT -> "TIMEOUT"
        GameWinReason.CHECKMATE -> "CHECKMATE"
        GameWinReason.RESIGNATION -> "RESIGNATION"
        GameWinReason.ABANDONMENT -> "ABANDONMENT"
        GameWinReason.DEATH -> "DEATH"
    }

    fun GameWinReason.Companion.fromDbString(string: String?) =
        GameWinReason.entries.find { it.toDbWinReason() == string }
        ?: throw IllegalArgumentException("Invalid db win reason")

    fun GameDrawReason.toDbWinReason() = when(this) {
        GameDrawReason.UNKNOWN -> "UNKNOWN"
        GameDrawReason.TIMEOUT_VS_INSUFFICIENT_MATERIAL -> "TIMEOUT_VS_INSUFFICIENT_MATERIAL"
        GameDrawReason.INSUFFICIENT_MATERIAL -> "INSUFFICIENT_MATERIAL"
        GameDrawReason.THREEFOLD_REPETITION -> "THREEFOLD_REPETITION"
        GameDrawReason.FIFTY_MOVE_RULE -> "FIFTY_MOVE_RULE"
        GameDrawReason.STALEMATE -> "STALEMATE"
    }

    fun GameDrawReason.Companion.fromDbString(string: String?) =
        GameDrawReason.entries.find { it.toDbWinReason() == string }
        ?: throw IllegalArgumentException("Invalid db draw reason")
}
