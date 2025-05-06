package pl.edu.uj.tcs.rchess.model.pieces
import pl.edu.uj.tcs.rchess.model.*

class Knight(private var square: Square, private var owner: PlayerColor) : Piece(square = square, owner = owner) {
    override fun getMoveVision(board: BoardState): List<Square> {
        TODO("Implement knight's move vision.")
    }

    override fun getCaptureVision(board: BoardState): List<Square> {
        TODO("Implement knight's capture vision.")
    }
}