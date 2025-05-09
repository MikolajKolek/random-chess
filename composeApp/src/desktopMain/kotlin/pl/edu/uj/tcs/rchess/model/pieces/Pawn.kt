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
                        listOfMoves += Move(square, square.copy(rank = 7), Move.Promotion.QUEEN)
                        listOfMoves += Move(square, square.copy(rank = 7), Move.Promotion.ROOK)
                        listOfMoves += Move(square, square.copy(rank = 7), Move.Promotion.BISHOP)
                        listOfMoves += Move(square, square.copy(rank = 7), Move.Promotion.KNIGHT)
                    } else {
                        listOfMoves += Move(square, square.copy(rank = square.rank+1))
                    }
                    if(square.rank == 1) {
                        if(board.getPieceAt(square.copy(rank = 3)) == null) {
                            listOfMoves += Move(square, square.copy(rank = square.rank+2))
                        }
                    }
                }
            } catch(_: Exception) {}
        } else {
            try {
                if(board.getPieceAt(square.copy(rank = square.rank-1)) == null) {
                    if(square.rank == 1){
                        listOfMoves += Move(square, square.copy(rank = 0), Move.Promotion.QUEEN)
                        listOfMoves += Move(square, square.copy(rank = 0), Move.Promotion.ROOK)
                        listOfMoves += Move(square, square.copy(rank = 0), Move.Promotion.BISHOP)
                        listOfMoves += Move(square, square.copy(rank = 0), Move.Promotion.KNIGHT)
                    } else {
                        listOfMoves += Move(square, square.copy(rank = square.rank-1))
                    }
                    if(square.rank == 6) {
                        if(board.getPieceAt(square.copy(rank = 4)) == null) {
                            listOfMoves += Move(square, square.copy(rank = square.rank-2))
                        }
                    }
                }
            } catch(_: Exception) {}
        }
        return listOfMoves.toList()
    }

    override fun getCaptureVision(board: BoardState, square: Square): List<Move> {
        var listSquares : Array<Square> = arrayOf()
        var validSquares : Array<Square> = arrayOf()
        if(owner == PlayerColor.WHITE) {
            listSquares += square.copy(rank = square.rank+1, file = square.file+1)
            listSquares += square.copy(rank = square.rank+1, file = square.file-1)
        } else {
            listSquares += square.copy(rank = square.rank-1, file = square.file+1)
            listSquares += square.copy(rank = square.rank-1, file = square.file-1)
        }

        for(sqr in listSquares) {
            if(board.enPassantTarget != null) {
                if(sqr == board.enPassantTarget) {
                    if(owner == PlayerColor.WHITE && sqr.rank == 5) {
                        validSquares = validSquares.plus(sqr)
                    } else if(owner == PlayerColor.BLACK && sqr.rank == 2) {
                        validSquares = validSquares.plus(sqr)
                    }
                    continue
                }
            }
            if(board.getPieceAt(sqr) != null) {
                if(board.getPieceAt(sqr)!!.owner != owner) {
                    validSquares = validSquares.plus(sqr)
                }
            }
        }

        var validMoves : Array<Move> = arrayOf()
        for(sqr in validSquares) {
            if(sqr.rank == 0 || sqr.rank == 7) {
                validMoves += Move(square, sqr, Move.Promotion.KNIGHT)
                validMoves += Move(square, sqr, Move.Promotion.BISHOP)
                validMoves += Move(square, sqr, Move.Promotion.ROOK)
                validMoves += Move(square, sqr, Move.Promotion.QUEEN)
            } else {
                validMoves += Move(square, sqr)
            }
        }

        return validMoves.toList()
    }

    override val fenLetterLowercase = 'p'
}
