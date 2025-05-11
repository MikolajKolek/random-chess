package pl.edu.uj.tcs.rchess.server

import java.time.LocalDateTime
import kotlinx.serialization.json.JsonObject
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.Move

data class ServiceGame(
    override val id: Int,
    override val moves: List<Move>,
    override val startingPosition: BoardState,
    override val creationDate: LocalDateTime,
    override val result: GameResult,
    override val metadata: JsonObject?,
    val gameIdInService: String?,
    val service: Service,
    val blackPlayer: ServiceAccount,
    val whitePlayer: ServiceAccount,
) : HistoryGame()