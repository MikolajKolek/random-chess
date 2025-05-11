package pl.edu.uj.tcs.rchess.model.pieces
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

class Queen(owner: PlayerColor): Piece(owner = owner) {
    override fun getMoveVision(boardState: BoardState, square: Square): List<Move> {
        return Bishop(owner).getMoveVision(boardState, square) + Rook(owner).getMoveVision(boardState, square)
    }

    override fun getCaptureVision(boardState: BoardState, square: Square): List<Move> {
        return Bishop(owner).getCaptureVision(boardState, square) + Rook(owner).getCaptureVision(boardState, square)
    }

    override val fenLetterLowercase = 'q'

    override val unicodeSymbol = when (owner) {
        PlayerColor.WHITE -> "♕"
        PlayerColor.BLACK -> "♛"
    }
}
