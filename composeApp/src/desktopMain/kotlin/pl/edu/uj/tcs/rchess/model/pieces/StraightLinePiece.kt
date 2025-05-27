package pl.edu.uj.tcs.rchess.model.pieces

import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square
import pl.edu.uj.tcs.rchess.model.state.BoardState

sealed class StraightLinePiece(owner: PlayerColor) : SameMoveCapturePiece(owner) {
    abstract val vectors: List<Square.Vector>

    override fun getVision(boardState: BoardState, square: Square): List<Square> =
        vectors.map { vector ->
            val squares = mutableListOf<Square?>()

            for(i in 1..7) {
                squares.add(square + (vector * i))
                if(squares.last()?.let { boardState.board[it] } != null)
                    break
            }

            squares
        }.flatten().filterNotNull()
}
