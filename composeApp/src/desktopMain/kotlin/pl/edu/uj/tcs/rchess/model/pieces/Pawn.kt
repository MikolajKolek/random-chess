package pl.edu.uj.tcs.rchess.model.pieces
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

class Pawn(owner: PlayerColor): Piece(owner = owner) {
    override fun getMoveVision(board: BoardState, square: Square): List<Move> {
        TODO("Implement pawn's move vision.")
    }

    override fun getCaptureVision(board: BoardState, square: Square): List<Move> {
        TODO("Implement pawn's capture vision.")
    }

    override val fenLetterLowercase = 'p'
}
