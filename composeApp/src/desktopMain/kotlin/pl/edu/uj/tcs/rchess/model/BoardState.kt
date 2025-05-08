package pl.edu.uj.tcs.rchess.model

class BoardState {
    var Board: Array<Array<Square>> = Array(8, { Array(8, { Square(1, 1) })})
    var currentTurn: PlayerColor = PlayerColor.WHITE
    private var enPassantTarget : Square? = null
        // The only square one may capture via en passant this turn.
    private var castlingRights : Array<Boolean> = Array(4, {true})
        // Order: White-Kingside, White-Queenside, Black-Kingside, Black-Queenside

    init {
        for(i in 1..8) {
            for(j in 1..8) {
                Board[i][j] = Square(i, j)
            }
        }
    }

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
