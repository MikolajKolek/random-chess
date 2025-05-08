package pl.edu.uj.tcs.rchess.model.pieces
import pl.edu.uj.tcs.rchess.model.*

class Pawn(square: SquarePosition, owner: PlayerColor): Piece(square = square, owner = owner) {
    override fun getMoveVision(board: BoardState): List<SquarePosition> {
        TODO("Implement bishop's move vision.")
    }

    override fun getCaptureVision(board: BoardState): List<SquarePosition> {
        TODO("Implement bishop's capture vision.")
    }
}
