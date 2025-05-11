package pl.edu.uj.tcs.rchess.components.board

import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.Square

class MoveInProgress(
    val startSquare: Square,
    val possibleMoves: List<Move>,
) {
    val targetSquares = possibleMoves.map { it.to }.toSet()
}
