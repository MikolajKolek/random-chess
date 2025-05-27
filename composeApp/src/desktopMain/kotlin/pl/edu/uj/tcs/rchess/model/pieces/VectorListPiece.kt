package pl.edu.uj.tcs.rchess.model.pieces

import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square
import pl.edu.uj.tcs.rchess.model.state.BoardState

sealed class VectorListPiece(owner: PlayerColor) : SameMoveCapturePiece(owner) {
    abstract val vectors: List<Square.Vector>

    override fun getVision(boardState: BoardState, square: Square): List<Square> =
        vectors.mapNotNull { square + it }
}
