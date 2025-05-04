package pl.edu.uj.tcs.rchess.model

/**
 * Class describing chess moves.
 * @param from Source square.
 * @param to Destination square.
 * @param promoteTo Piece the pawn promotes to (null if inapplicable).
 */
class Move(var from: Square, var to: Square, var promoteTo: Piece?) {

    init {
        // TODO: Prevent promotion to a king or pawn.
        // TODO: Prevent promotion of any piece other than a pawn.
        // TODO: Prevent moving a pawn to the last rank without promoting.
    }
}
