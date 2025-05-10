package pl.edu.uj.tcs.rchess.server

import java.time.LocalDateTime
import kotlinx.serialization.json.JsonObject
import pl.edu.uj.tcs.rchess.model.GameResult

data class ServiceGame(
    override val id: Int,
    override val moves: String,
    override val creationDate: LocalDateTime,
    override val result: GameResult,
    override val metadata: JsonObject?,
    val gameIdInService: String?,
    val service: Service,
    val blackPlayer: ServiceAccount,
    val whitePlayer: ServiceAccount,
) : HistoryGame()