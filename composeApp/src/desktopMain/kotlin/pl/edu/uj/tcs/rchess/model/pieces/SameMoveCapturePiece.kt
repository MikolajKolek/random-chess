package pl.edu.uj.tcs.rchess.model.pieces

import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

abstract class SameMoveCapturePiece(owner: PlayerColor) : Piece(owner) {
    abstract fun getVision(boardState: BoardState, square: Square): List<Square>

    override fun getMoveVision(boardState: BoardState, square: Square): List<Move> =
        getVision(boardState, square)
            .filter { boardState.board[it] == null }
            .map { Move(square, it) }

    override fun getCaptureVision(boardState: BoardState, square: Square): List<Move> =
        getVision(boardState, square)
            .filter { boardState.board[it]?.owner == owner.opponent }
            .map { Move(square, it) }
}
