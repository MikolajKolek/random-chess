package pl.edu.uj.tcs.rchess.model.pieces
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

class Pawn(owner: PlayerColor): Piece(owner = owner) {
    override fun getMoveVision(board: BoardState, square: Square): List<Move> {
        var validSquares : Array<Square> = arrayOf()

        if(owner == PlayerColor.WHITE) {
            if(board.getPieceAt(square.copy(rank = square.rank + 1)) != null) {
                validSquares += square.copy(rank = square.rank + 1)
                if (board.getPieceAt(square.copy(rank = square.rank + 1)) == null && square.rank == 1) {
                    validSquares += square.copy(rank = 3)
                }
            }
        } else {
            if(board.getPieceAt(square.copy(rank = square.rank - 1)) != null) {
                validSquares += square.copy(rank = square.rank - 1)
                if (board.getPieceAt(square.copy(rank = square.rank - 1)) == null && square.rank == 6) {
                    validSquares += square.copy(rank = 4)
                }
            }
        }

        var listOfMoves: Array<Move> = arrayOf()
        for(sqr in validSquares) {
            if((sqr.rank == 7 && owner == PlayerColor.WHITE) || (sqr.rank == 1 && owner == PlayerColor.BLACK)) {
                listOfMoves += Move(square, sqr, Move.Promotion.KNIGHT)
                listOfMoves += Move(square, sqr, Move.Promotion.BISHOP)
                listOfMoves += Move(square, sqr, Move.Promotion.ROOK)
                listOfMoves += Move(square, sqr, Move.Promotion.QUEEN)
            } else {
                listOfMoves += Move(square, sqr)
            }
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
