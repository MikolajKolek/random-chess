package pl.edu.uj.tcs.rchess.server

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import pl.edu.uj.tcs.rchess.db.tables.records.PgnGamesRecord
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Fen.Companion.fromFen
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.Move
import java.time.LocalDateTime

data class PgnGame(
    override val id: Int,
    override val moves: List<Move>,
    override val startingPosition: BoardState,
    override val creationDate: LocalDateTime,
    override val result: GameResult,
    override val metadata: JsonObject?,
    val blackPlayerName: String,
    val whitePlayerName: String,
) : HistoryGame() {
    constructor(resultRow: PgnGamesRecord) : this(
        id = resultRow.id!!,
        moves = resultRow.moves.map { Move.fromLongAlgebraicNotation(it!!) },
        startingPosition = BoardState.fromFen(resultRow.startingPosition),
        creationDate = resultRow.creationDate,
        result = GameResult.fromDbResult(resultRow.result),
        metadata = resultRow.metadata?.data()?.let { Json.decodeFromString(it) },
        blackPlayerName = resultRow.blackPlayerName,
        whitePlayerName = resultRow.whitePlayerName
    )
}