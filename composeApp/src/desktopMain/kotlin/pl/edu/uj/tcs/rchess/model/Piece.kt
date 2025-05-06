package pl.edu.uj.tcs.rchess.model

/**
 * Abstract class describing all chess pieces.
 * @param square Square that initially contains the piece.
 * @param owner The color of the piece.
 */
abstract class Piece(private var square: Square, private var owner: PlayerColor) {

    /**
     * @param board The board that this piece is on.
     * @return List of all possible and legal moves that this piece can currently perform.
     */
    fun getColor(): PlayerColor = owner

    fun getSquare() : Square = square

    fun getLegalMoves(board: BoardState): List<Move> {
        return listOf();
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
