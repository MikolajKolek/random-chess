package pl.edu.uj.tcs.rchess.model

class BoardState {
    val board: Array<Array<Square>> = Array(8) { i ->
        Array(8) { j ->
            Square(SquarePosition(i, j))
        }
    }
    var currentTurn: PlayerColor = PlayerColor.WHITE
    private var enPassantTarget : Square? = null
        // The only square one may capture via en passant this turn.

    private var castlingRights: CastlingRights = CastlingRights.full()

    /**
     * @param move The move to apply.
     * @throws IllegalStateException when the current position is not legal.
     * @throws IllegalArgumentException when the given move is not legal.
     * @return The current BoardState.
     */
    fun applyMove(move: Move) : BoardState {
        return this
    }

    /**
     * @param move The move to apply.
     * @throws IllegalStateException when the current position is not legal.
     * @throws IllegalArgumentException when the given move is not a valid move in this GameState.
     * @return A copy of the current BoardState after having applied the given move.
     */
    /*fun forceMoveAndCopy(move : Move) : BoardState {
        return BoardState(this)
    }*/

    /**
     * @return Returns true if the current position is legal
     */
    fun isLegal() : Boolean {
        return false
    }

    /**
     * @param move The move to verify.
     * @return Returns true if the given move is legal in this position.
     */
    fun isLegalMove(move : Move) : Boolean {
        return false
    }
}
// TODO: Implement the logic managing BoardState
