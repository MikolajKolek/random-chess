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
    val enPassantTarget: Square?,
    val castlingRights: CastlingRights,
    val halfmoveCounter: Int,
    val fullmoveNumber: Int
) {
    companion object {
        fun empty() = BoardState(
            board = List(64) { null },
            currentTurn = PlayerColor.WHITE,
            enPassantTarget = null,
            castlingRights = CastlingRights.full(),
            halfmoveCounter = 0,
            fullmoveNumber = 1
        )

        fun fromFen(fen: FEN): BoardState {
            TODO()
        }
    }

    fun getPieceAt(position: Square) = board[position.row * 8 + position.col]

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
    fun getLegalMovesFor(square: Square): List<Move> {
        return getPieceAt(square)?.getLegalMoves(this) ?: emptyList()
    }

    fun toFen(): String {
        TODO()
    }
}
// TODO: Implement the logic managing BoardState
