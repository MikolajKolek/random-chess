package pl.edu.uj.tcs.rchess.api.entity.game

import pl.edu.uj.tcs.rchess.api.entity.Opening
import pl.edu.uj.tcs.rchess.api.entity.Service
import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.api.entity.ranking.RankingUpdate
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.state.BoardState
import java.time.OffsetDateTime

/**
 * A service game committed to the database
 */
data class HistoryServiceGame(
    override val id: Int,
    override val moves: List<Move>,
    override val startingPosition: BoardState,
    override val finalPosition: BoardState,
    override val opening: Opening?,
    override val creationDate: OffsetDateTime,
    override val result: GameResult,
    override val metadata: Map<String, String>,
    val gameIdInService: String?,
    val service: Service,
    override val blackPlayer: ServiceAccount,
    override val whitePlayer: ServiceAccount,
    override val clockSettings: ClockSettings?,
    val rankingUpdates: List<RankingUpdate>
) : HistoryGame(), ServiceGame
