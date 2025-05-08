package pl.edu.uj.tcs.rchess.model

import pl.edu.uj.tcs.rchess.model.pieces.Piece

/**
 * Immutable class describing the state of the chessboard.
 *
 * Can be exactly serialized to/from FEN.
 */
class BoardState(
    private val board: List<Piece?>,
    val currentTurn: PlayerColor,
    val enPassantTarget: SquarePosition?,
    val castlingRights: CastlingRights,
) {
    companion object {
        fun empty() = BoardState(
            board = List(64) { null },
            currentTurn = PlayerColor.WHITE,
            enPassantTarget = null,
            castlingRights = CastlingRights.full(),
        )

        fun fromFen(fen: String): BoardState {
            TODO()
        }
    }

    fun getPieceAt(position: SquarePosition) = board[position.row * 8 + position.col]

    /**
     * @param move The move to apply.
     * @throws IllegalStateException when the current position is not legal.
     * @throws IllegalArgumentException when the given move is not legal.
     * @return The current BoardState.
     */
    fun applyMove(move: Move) : BoardState {
        TODO()
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
        TODO()
    }

    /**
     * @param move The move to verify.
     * @return Returns true if the given move is legal in this position.
     */
    fun isLegalMove(move : Move) : Boolean {
        TODO()
    }

    /**
     * @return List of legal moves for the piece at the given position.
     * If there is no piece at the position, return an empty list.
     */
    // TODO: Is this function necessary? Shouldn't it be a responsibility of the Piece class?
    fun getPossibleMovesFor(position: SquarePosition): List<Move> {
        TODO()
    }

    fun toFen(): String {
        TODO()
    }
}
// TODO: Implement the logic managing BoardState
