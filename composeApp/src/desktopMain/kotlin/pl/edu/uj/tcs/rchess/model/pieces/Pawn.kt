package pl.edu.uj.tcs.rchess.model.pieces
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

class Pawn(owner: PlayerColor): Piece(owner = owner) {
    override fun getMoveVision(board: BoardState, square: Square): List<Move> {
        var listOfMoves : Array<Move> = arrayOf()
        if(owner == PlayerColor.WHITE) {
            try {
                if(board.getPieceAt(square.copy(rank = square.rank+1)) == null) {
                    if(square.rank == 6){
                        listOfMoves = listOfMoves.plus(Move(square, square.copy(rank = 7), Move.Promotion.QUEEN))
                        listOfMoves = listOfMoves.plus(Move(square, square.copy(rank = 7), Move.Promotion.ROOK))
                        listOfMoves = listOfMoves.plus(Move(square, square.copy(rank = 7), Move.Promotion.BISHOP))
                        listOfMoves = listOfMoves.plus(Move(square, square.copy(rank = 7), Move.Promotion.KNIGHT))
                    } else {
                        listOfMoves = listOfMoves.plus(Move(square, square.copy(rank = square.rank+1), null))
                    }
                    if(square.rank == 1) {
                        if(board.getPieceAt(square.copy(rank = 3)) == null) {
                            listOfMoves = listOfMoves.plus(Move(square, square.copy(rank = square.rank+2), null))
                        }
                    }
                }
            } catch(_: Exception) {}
        } else {
            try {
                if(board.getPieceAt(square.copy(rank = square.rank-1)) == null) {
                    if(square.rank == 1){
                        listOfMoves = listOfMoves.plus(Move(square, square.copy(rank = 0), Move.Promotion.QUEEN))
                        listOfMoves = listOfMoves.plus(Move(square, square.copy(rank = 0), Move.Promotion.ROOK))
                        listOfMoves = listOfMoves.plus(Move(square, square.copy(rank = 0), Move.Promotion.BISHOP))
                        listOfMoves = listOfMoves.plus(Move(square, square.copy(rank = 0), Move.Promotion.KNIGHT))
                    } else {
                        listOfMoves = listOfMoves.plus(Move(square, square.copy(rank = square.rank-1), null))
                    }
                    if(square.rank == 6) {
                        if(board.getPieceAt(square.copy(rank = 4)) == null) {
                            listOfMoves = listOfMoves.plus(Move(square, square.copy(rank = square.rank-2), null))
                        }
                    }
                }
            } catch(_: Exception) {}
        }
        return listOfMoves.toList()
    }

    override fun getCaptureVision(board: BoardState, square: Square): List<Move> {
        TODO()
    }

    override val fenLetterLowercase = 'p'
}
