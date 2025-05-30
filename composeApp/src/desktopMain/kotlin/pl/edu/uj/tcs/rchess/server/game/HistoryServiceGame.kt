package pl.edu.uj.tcs.rchess.server.game

import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.state.BoardState
import pl.edu.uj.tcs.rchess.server.Opening
import pl.edu.uj.tcs.rchess.server.Service
import pl.edu.uj.tcs.rchess.server.ServiceAccount
import java.time.LocalDateTime

/**
 * A service game commited to the database
 */
data class HistoryServiceGame(
    override val id: Int,
    override val moves: List<Move>,
    override val startingPosition: BoardState,
//    override val finalPosition: BoardState,
    override val creationDate: LocalDateTime,
    override val result: GameResult,
    override val metadata: Map<String, String>,
    override val opening: Opening?,
    val gameIdInService: String?,
    val service: Service,
    override val blackPlayer: ServiceAccount,
    override val whitePlayer: ServiceAccount,
) : HistoryGame(), ServiceGame
