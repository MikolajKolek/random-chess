package pl.edu.uj.tcs.rchess.server

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import pl.edu.uj.tcs.rchess.db.tables.records.PgnGamesRecord
import java.time.LocalDateTime

data class PgnGame(
    override val id: Int,
    override val moves: String,
    override val date: LocalDateTime?,
    override val metadata: JsonObject?,
    val blackPlayerName: String,
    val whitePlayerName: String
) : HistoryGame() {
    constructor(resultRow: PgnGamesRecord) : this(
        id = resultRow.id!!,
        moves = resultRow.moves,
        date = resultRow.date,
        metadata = resultRow.metadata?.data()?.let { Json.decodeFromString(it) },
        blackPlayerName = resultRow.blackPlayerName,
        whitePlayerName = resultRow.whitePlayerName
    )
}