package pl.edu.uj.tcs.rchess.server

import java.time.LocalDateTime
import kotlinx.serialization.json.JsonObject

data class ServiceGame(
    override val id: Int,
    override val moves: String,
    override val date: LocalDateTime?,
    override val metadata: JsonObject?,
    val gameIdInService: String?,
    val service: Service,
    val blackPlayer: ServiceAccount,
    val whitePlayer: ServiceAccount,
) : HistoryGame()