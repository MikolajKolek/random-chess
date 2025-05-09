package pl.edu.uj.tcs.rchess.server

import kotlinx.serialization.json.JsonObject
import java.time.LocalDateTime

sealed class HistoryGame {
    abstract val id: Int
    abstract val moves: String
    abstract val date: LocalDateTime?
    abstract val metadata: JsonObject?
}
