package pl.edu.uj.tcs.rchess.components.board

import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.Square
import pl.edu.uj.tcs.rchess.model.pieces.Piece

class MoveInProgress(
    val startSquare: Square,
    val startPiece: Piece,
    val possibleMoves: List<Move>,
) {
    val targetSquares = possibleMoves.map { it.to }.toSet()
}
