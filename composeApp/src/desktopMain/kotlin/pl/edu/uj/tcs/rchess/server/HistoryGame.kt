package pl.edu.uj.tcs.rchess.server

import java.time.LocalDateTime
import kotlinx.serialization.json.JsonObject

abstract class HistoryGame {
    abstract val id: Int
    abstract val moves: String
    abstract val date: LocalDateTime?
    abstract val metadata: JsonObject?
}