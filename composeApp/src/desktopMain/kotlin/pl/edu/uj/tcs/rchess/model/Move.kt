package pl.edu.uj.tcs.rchess.model

/**
 * Data class describing chess moves. It's the responsibility of the game logic to verify if the move is valid.
 *
 * @param from Source square.
 * @param to Destination square.
 * @param promoteTo Piece the pawn promotes to (null if inapplicable).
 */
data class Move(val from: Square, val to: Square, val promoteTo: Promotion? = null) {
    enum class Promotion {
        QUEEN,
        ROOK,
        BISHOP,
        KNIGHT
    }

    init {
        require(from != to) { "From and to positions are the same." }
        // Move verification is managed by BoardState - is can only be done in context of the board.
    }

    fun toLongAlgebraicNotation() : String {
        TODO()
    }

    companion object {
        fun fromLongAlgebraicNotation(move: String) : Move {
            TODO()
        }
    }
}
