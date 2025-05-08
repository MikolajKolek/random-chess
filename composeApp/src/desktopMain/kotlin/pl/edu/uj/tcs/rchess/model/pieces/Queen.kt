package pl.edu.uj.tcs.rchess.model.pieces
import pl.edu.uj.tcs.rchess.model.*

class Queen(square: Square, owner: PlayerColor): Piece(square = square, owner = owner) {
    override fun getMoveVision(board: BoardState, square: Square): List<Square> {
        TODO("Implement bishop's move vision.")
    }

    override fun getCaptureVision(board: BoardState, square: Square): List<Square> {
        TODO("Implement bishop's capture vision.")
    }
}
