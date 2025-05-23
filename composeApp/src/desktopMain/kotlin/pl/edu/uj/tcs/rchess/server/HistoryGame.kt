package pl.edu.uj.tcs.rchess.server

import kotlinx.serialization.json.JsonObject
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.Move
import java.time.LocalDateTime

sealed class HistoryGame {
    abstract val id: Int
    abstract val moves: List<Move>
    abstract val startingPosition: BoardState
    abstract val finalPosition: BoardState
    abstract val creationDate: LocalDateTime
    abstract val result: GameResult
    abstract val metadata: JsonObject?
}
