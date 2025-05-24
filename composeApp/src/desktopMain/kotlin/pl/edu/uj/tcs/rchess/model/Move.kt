package pl.edu.uj.tcs.rchess.model

import pl.edu.uj.tcs.rchess.model.pieces.Bishop
import pl.edu.uj.tcs.rchess.model.pieces.Knight
import pl.edu.uj.tcs.rchess.model.pieces.Queen
import pl.edu.uj.tcs.rchess.model.pieces.Rook

/**
 * Data class describing chess moves. It's the responsibility of the game logic to verify if the move is valid.
 *
 * @param from Source square.
 * @param to Destination square.
 * @param promoteTo Piece the pawn promotes to (null if inapplicable).
 */
data class Move(val from: Square, val to: Square, val promoteTo: Promotion? = null) {
    enum class Promotion(val identifier: Char) {
        QUEEN('q'),
        ROOK('r'),
        BISHOP('b'),
        KNIGHT('n');

        fun toPiece(owner: PlayerColor) = when (this) {
            QUEEN -> Queen(owner)
            ROOK -> Rook(owner)
            BISHOP -> Bishop(owner)
            KNIGHT -> Knight(owner)
        }

        companion object {
            fun fromIdentifier(identifier: Char) : Promotion? = entries.find { it.identifier == identifier }
                ?: throw IllegalArgumentException("Invalid piece identifier : $identifier")
        }
    }

    init {
        require(from != to) { "From and to positions are the same." }
        // Move verification is managed by BoardState - it can only be done in the context of the board.
    }

    fun toLongAlgebraicNotation() =
        "$from${to}" + (promoteTo?.identifier ?: "")

    companion object {
        fun fromLongAlgebraicNotation(move: String) : Move {
            require(move.length == 4 || move.length == 5) {
                "Long algebraic notation must have 4 or 5 characters."
            }

            return Move(
                Square.fromString(move.substring(0, 2)),
                Square.fromString(move.substring(2, 4)),
                move.getOrNull(4)?.let { Promotion.fromIdentifier(it) }
            )
        }
    }
}
