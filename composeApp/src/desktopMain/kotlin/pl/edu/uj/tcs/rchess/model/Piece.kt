package pl.edu.uj.tcs.rchess.model

/**
 * Abstract class describing all chess pieces.
 * @param square Square that initially contains the piece.
 * @param owner The color of the piece.
 */
abstract class Piece(private var square: Square, private var owner: PlayerColor) {
    /**
     * @param board The board that this piece is on.
     * @return List of all available legal moves that piece can perform.
     */

    fun getLegalDestinations(board: BoardState): List<Square> {
        // TODO: Implement this for all 6 pieces.
        return listOf()
    }
}
