package pl.edu.uj.tcs.rchess.model.pieces

import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

class King(owner: PlayerColor): Piece(owner = owner) {
    val vectors = listOf(
        Square.Vector(1, 1),
        Square.Vector(1, 0),
        Square.Vector(1, -1),
        Square.Vector(0, 1),
        Square.Vector(0, -1),
        Square.Vector(-1, 1),
        Square.Vector(-1, 0),
        Square.Vector(-1, -1),
    )

    override fun getMoveVision(board: BoardState, square: Square): List<Move> {
        val ret = mutableListOf<Move>()
        for(v in vectors) {
            if(board.getPieceAt(Square(square.rank+v.rank, square.file+v.file)) == null)
                ret.add(Move(square, Square(square.rank+v.rank, square.file+v.file)))
        }
        if(owner == PlayerColor.WHITE) {
            if(board.castlingRights.whiteKingSide) ret.add(Move(Square.fromString("e1"), Square.fromString("g1")))
            if(board.castlingRights.whiteQueenSide) ret.add(Move(Square.fromString("e1"), Square.fromString("c1")))
        } else {
            if(board.castlingRights.blackKingSide) ret.add(Move(Square.fromString("e8"), Square.fromString("g8")))
            if(board.castlingRights.blackQueenSide) ret.add(Move(Square.fromString("e8"), Square.fromString("c8")))
        }
        return ret
    }

    override fun getCaptureVision(board: BoardState, square: Square): List<Move> {
        val ret = mutableListOf<Move>()
        for(v in vectors) {
            if(board.getPieceAt(Square(square.rank+v.rank, square.file+v.file)) != null)
                ret.add(Move(square, Square(square.rank+v.rank, square.file+v.file)))
        }
        return ret
    }

    override val fenLetterLowercase = 'k'
}
