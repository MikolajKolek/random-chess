package pl.edu.uj.tcs.rchess.server.game

import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.state.BoardState
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import pl.edu.uj.tcs.rchess.model.state.GameState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.min

/**
 * A game commited to the database
 */
sealed class HistoryGame : ApiGame {
    abstract val id: Int
    abstract val moves: List<Move>
    abstract val startingPosition: BoardState
    // TODO: Make abstract an initialize from the database
    val finalPosition: BoardState by lazy {
        finalGameState.currentState
    }
    abstract val creationDate: LocalDateTime
    abstract val result: GameResult
    abstract val metadata: Map<String, String>

    fun toPgnString(): String = buildString {
        fun appendTag(key: String, value: String) {
            append("[$key \"$value\"]\n")
        }

        val strippedMetadata = metadata.toMutableMap()
        fun appendMetadataTagOr(key: String, alternative: String) {
            metadata[key]?.let {
                appendTag(key, it)
                strippedMetadata.remove(key)
            }

            if (!metadata.contains(key))
                appendTag(key, alternative)
        }

        appendMetadataTagOr("Event", "?")
        appendMetadataTagOr("Site", "?")
        appendMetadataTagOr(
            "Date",
            creationDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
        )
        appendMetadataTagOr("Round", "?")
        appendMetadataTagOr("White", getPlayerName(PlayerColor.WHITE))
        appendMetadataTagOr("Black", getPlayerName(PlayerColor.BLACK))
        appendMetadataTagOr("Result", result.toPgnString())

        if (!metadata.contains("FEN") && startingPosition.toFenString() != BoardState.initial.toFenString()) {
            appendTag("FEN", startingPosition.toFenString())
        }

        strippedMetadata.forEach { appendTag(it.key, it.value) }

        append("\n")

        if (moves.isEmpty()) {
            append(result.toPgnString())
            return@buildString
        }

        var moveIndex = 0
        var fullMoveNumber = 1
        var boardState: BoardState = startingPosition
        if (startingPosition.currentTurn == PlayerColor.BLACK) {
            append("${fullMoveNumber++}... ${boardState.moveToStandardAlgebraic(moves[0])} ")
            boardState = boardState.applyMove(moves[0])
            moveIndex++
        }

        while (moveIndex <= moves.lastIndex) {
            append("${fullMoveNumber++}. ")

            for (i in moveIndex..min(moveIndex + 1, moves.lastIndex)) {
                append("${boardState.moveToStandardAlgebraic(moves[i])} ")
                boardState = boardState.applyMove(moves[i])
                moveIndex++
            }
        }

        append(result.toPgnString())
    }

    val finalGameState: GameState by lazy {
        GameState.finished(
            initialBoardState = startingPosition,
            moves = moves,
            finishedProgress = GameProgress.Finished(
                result = result
            ),
        )
    }
}
