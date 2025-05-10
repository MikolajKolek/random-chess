package pl.edu.uj.tcs.rchess.model.pieces
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

class Queen(owner: PlayerColor): Piece(owner = owner) {
    override fun getMoveVision(board: BoardState, square: Square): List<Move> {
        return Bishop(owner).getMoveVision(board, square).plus(Rook(owner).getMoveVision(board, square))
    }

    override fun getCaptureVision(board: BoardState, square: Square): List<Move> {
        return Bishop(owner).getCaptureVision(board, square).plus(Rook(owner).getCaptureVision(board, square))
    }

    override val fenLetterLowercase = 'q'
}
