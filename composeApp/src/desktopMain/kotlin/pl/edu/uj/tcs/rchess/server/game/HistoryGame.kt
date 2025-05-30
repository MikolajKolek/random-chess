package pl.edu.uj.tcs.rchess.server.game

import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.SanFullMove
import pl.edu.uj.tcs.rchess.model.state.BoardState
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import pl.edu.uj.tcs.rchess.model.state.GameState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * A game committed to the database
 */
sealed class HistoryGame : ApiGame {
    abstract val id: Int
    abstract val moves: List<Move>
    abstract val startingPosition: BoardState
    // TODO: Make abstract and initialize from the database
    val finalPosition: BoardState by lazy {
        finalGameState.currentState
    }
    abstract val creationDate: LocalDateTime
    abstract val result: GameResult
    abstract val metadata: Map<String, String>

    /**
     * Constructs the PGN metadata header for this game header.
     * Most fields are copied from the existing metadata field.
     *
     * This method must be called in the [buildString] function.
     */
    private fun StringBuilder.buildPgnHeader() {
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
    }

    val pgnString: String by lazy {
        buildString {
            buildPgnHeader()
            append("\n")

            if (moves.isEmpty()) {
                append(result.toPgnString())
                return@buildString
            }

            finalGameState.fullMoves.forEach { move ->
                append(move.number)
                when (move) {
                    is SanFullMove.InitialBlackMove -> {
                        append("...")
                    }
                    is SanFullMove.WithWhiteMove -> {
                        append(". ")
                        append(move.white.san)
                    }
                }
                move.black?.let {
                    append(' ')
                    append(it.san)
                }
                append(' ')
            }

            append(result.toPgnString())
        }
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
