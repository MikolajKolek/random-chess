package pl.edu.uj.tcs.rchess.viewmodel.board

import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.Square
import pl.edu.uj.tcs.rchess.model.pieces.Piece

sealed interface MoveInProgress {
    val startPiece: Piece
    val startSquare: Square

    class FirstPicked(
        override val startPiece: Piece,
        override val startSquare: Square,
        val possibleMoves: List<Move>,
    ): MoveInProgress {
        val targetSquares by lazy {
            possibleMoves.map { it.to }.toSet()
        }
    }

    class Promotion(
        override val startPiece: Piece,
        override val startSquare: Square,
        val promotionMoves: List<Move>,
    ): MoveInProgress
}
