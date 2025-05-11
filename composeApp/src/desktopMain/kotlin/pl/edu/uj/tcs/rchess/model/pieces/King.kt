package pl.edu.uj.tcs.rchess.model.pieces

import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

class King(owner: PlayerColor): VectorListPiece(owner = owner) {
    override val vectors = listOf(
        Square.Vector(1, 1),
        Square.Vector(1, 0),
        Square.Vector(1, -1),
        Square.Vector(0, 1),
        Square.Vector(0, -1),
        Square.Vector(-1, 1),
        Square.Vector(-1, 0),
        Square.Vector(-1, -1),
    )

    override fun getMoveVision(boardState: BoardState, square: Square): List<Move> {
        val ret = super.getMoveVision(boardState, square).toMutableList()

        if(owner == PlayerColor.WHITE) {
            if(boardState.castlingRights.whiteKingSide)
                if(boardState.board[Square.fromString("f1")] == null)
                    ret.add(Move(Square.fromString("e1"), Square.fromString("g1")))
            if(boardState.castlingRights.whiteQueenSide)
                if(boardState.board[Square.fromString("d1")] == null)
                    ret.add(Move(Square.fromString("e1"), Square.fromString("c1")))
        }
        else {
            if(boardState.castlingRights.blackKingSide)
                if(boardState.board[Square.fromString("f8")] == null)
                    ret.add(Move(Square.fromString("e8"), Square.fromString("g8")))
            if(boardState.castlingRights.blackQueenSide)
                if(boardState.board[Square.fromString("d8")] == null)
                    ret.add(Move(Square.fromString("e8"), Square.fromString("c8")))
        }

        return ret
    }

    override val fenLetterLowercase = 'k'

    override val unicodeSymbol = when (owner) {
        PlayerColor.WHITE -> "♔"
        PlayerColor.BLACK -> "♚"
    }
}
