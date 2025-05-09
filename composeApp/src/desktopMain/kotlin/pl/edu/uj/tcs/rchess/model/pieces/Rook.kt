package pl.edu.uj.tcs.rchess.model.pieces
import pl.edu.uj.tcs.rchess.model.*

class Rook(owner: PlayerColor): Piece(owner = owner) {
    override fun getMoveVision(board: BoardState, square: Square): List<Move> {
        TODO("Implement bishop's move vision.")
    }

    override fun getCaptureVision(board: BoardState, square: Square): List<Move> {
        TODO("Implement bishop's capture vision.")
    }
}
