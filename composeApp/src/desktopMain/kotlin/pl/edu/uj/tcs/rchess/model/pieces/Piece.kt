package pl.edu.uj.tcs.rchess.model.pieces

import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

/**
 * Sealed class describing all chess pieces.
 * @param square Square that initially contains the piece.
 * @param owner The color of the piece.
 */
sealed class Piece(
    /**
     * The square that the piece is on.
     */
    val square: Square,

    /**
     * The color of the piece.
     */
    val owner: PlayerColor,
) {
    /**
     * @param board The board that this piece is on.
     * @return List of all possible and legal moves that this piece can currently perform.
     */
    fun getLegalMoves(board: BoardState): List<Move> {
        TODO()
        // Gets the sum of CaptureVision and MoveVision, and excludes the illegal moves.
    }

    /**
     * @param board The board that this piece is on.
     * @return List of all squares we can capture on - or check, if the King is within the capture vision.
     */
    abstract fun getCaptureVision(board: BoardState): List<Square>

    /**
     * @param board The board that this piece is on.
     * @return List of all squares we can move to without capturing.
     */
    abstract fun getMoveVision(board: BoardState): List<Square>
}
