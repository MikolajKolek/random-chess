package pl.edu.uj.tcs.rchess.model.pieces

import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

abstract class VectorListPiece(owner: PlayerColor) : SameMoveCapturePiece(owner) {
    abstract val vectors: List<Square.Vector>

    override fun getVision(board: BoardState, square: Square): List<Square> =
        vectors.mapNotNull { square + it }
}