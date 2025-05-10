package pl.edu.uj.tcs.rchess.server

import kotlinx.serialization.json.JsonObject
import pl.edu.uj.tcs.rchess.model.GameResult
import java.time.LocalDateTime

sealed class HistoryGame {
    abstract val id: Int
    abstract val moves: String
    abstract val creationDate: LocalDateTime
    abstract val result: GameResult
    abstract val metadata: JsonObject?
}
