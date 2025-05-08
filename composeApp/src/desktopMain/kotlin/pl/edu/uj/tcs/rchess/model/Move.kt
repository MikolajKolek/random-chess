package pl.edu.uj.tcs.rchess.model

/**
 * Data class describing chess moves. It's the responsibility of the game logic to verify if the move is valid.
 *
 * @param from Source square.
 * @param to Destination square.
 * @param promoteTo Piece the pawn promotes to (null if inapplicable).
 */
data class Move(val from: SquarePosition, val to: SquarePosition, val promoteTo: Promotion?) {
    enum class Promotion {
        QUEEN,
        ROOK,
        BISHOP,
        KNIGHT
    }

    init {
        require(from != to) { "From and to positions are the same." }

        // change: These changes should be verified when applying, since we don't know what piece is at the from position
        // TODO: Prevent promotion of any piece other than a pawn.
        // TODO: Prevent moving a pawn to the last rank without promoting.
    }
}
