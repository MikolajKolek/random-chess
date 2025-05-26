package pl.edu.uj.tcs.rchess.server

import kotlinx.serialization.json.Json
import pl.edu.uj.tcs.rchess.db.tables.records.PgnGamesRecord
import pl.edu.uj.tcs.rchess.model.Fen.Companion.fromFen
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.state.BoardState
import java.time.LocalDateTime

data class PgnGame(
    override val id: Int,
    override val moves: List<Move>,
    override val startingPosition: BoardState,
    override val finalPosition: BoardState,
    override val creationDate: LocalDateTime,
    override val result: GameResult,
    override val metadata: Map<String, String>,
    val blackPlayerName: String,
    val whitePlayerName: String,
) : HistoryGame {
    //TODO: don't do this here maybe
    constructor(resultRow: PgnGamesRecord) : this(
        id = resultRow.id!!,
        moves = resultRow.moves.map { Move.fromLongAlgebraicNotation(it!!) },
        startingPosition = BoardState.fromFen(resultRow.startingPosition),
        // TODO: Use data from a generated column in the database
        finalPosition = BoardState.initial,
        creationDate = resultRow.creationDate,
        result = GameResult.fromDbResult(resultRow.result),
        metadata = resultRow.metadata?.data()?.let { Json.decodeFromString<Map<String, String>>(it) } ?: emptyMap(),
        blackPlayerName = resultRow.blackPlayerName,
        whitePlayerName = resultRow.whitePlayerName
    )

    override fun getPlayerName(playerColor: PlayerColor): String =
        when (playerColor) {
            PlayerColor.BLACK -> blackPlayerName
            PlayerColor.WHITE -> whitePlayerName
        }
}
