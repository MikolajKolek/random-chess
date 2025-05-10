package pl.edu.uj.tcs.rchess.model.pieces

import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

abstract class SameMoveCapturePiece(owner: PlayerColor) : Piece(owner) {
    abstract fun getVision(board: BoardState, square: Square): List<Square>

    override fun getMoveVision(board: BoardState, square: Square): List<Move> =
        getVision(board, square)
            .filter { board.getPieceAt(it) == null }
            .map { Move(square, it) }

    override fun getCaptureVision(board: BoardState, square: Square): List<Move> =
        getVision(board, square)
            .filter { board.getPieceAt(it)?.owner != owner }
            .map { Move(square, it) }
}