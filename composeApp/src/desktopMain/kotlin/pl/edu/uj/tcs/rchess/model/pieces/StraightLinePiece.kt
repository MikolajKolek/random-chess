package pl.edu.uj.tcs.rchess.model.pieces

import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

abstract class StraightLinePiece(owner: PlayerColor) : SameMoveCapturePiece(owner) {
    abstract val vectors: List<Square.Vector>

    override fun getVision(board: BoardState, square: Square): List<Square> =
        vectors.map { vector ->
            val squares = mutableListOf<Square?>()

            for(i in 1..7) {
                squares.add(square + (vector * i))
                if(board.getPieceAt(squares.last()) != null)
                    break
            }

            squares
        }.flatten().filterNotNull()
}