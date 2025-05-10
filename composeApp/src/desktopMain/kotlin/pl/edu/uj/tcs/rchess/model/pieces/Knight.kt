package pl.edu.uj.tcs.rchess.model.pieces
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

class Knight(owner: PlayerColor): Piece(owner = owner) {
    private fun getVision(square: Square): List<Square> {
        var listSquares : Array<Square> = arrayOf()
        try { listSquares += Square(rank = square.rank+1, file = square.file+2) } catch (_: Exception) {}
        try { listSquares += Square(rank = square.rank+1, file = square.file-2) } catch (_: Exception) {}
        try { listSquares += Square(rank = square.rank+2, file = square.file+1) } catch (_: Exception) {}
        try { listSquares += Square(rank = square.rank+2, file = square.file-1) } catch (_: Exception) {}
        try { listSquares += Square(rank = square.rank-1, file = square.file+2) } catch (_: Exception) {}
        try { listSquares += Square(rank = square.rank-1, file = square.file-2) } catch (_: Exception) {}
        try { listSquares += Square(rank = square.rank-2, file = square.file+1) } catch (_: Exception) {}
        try { listSquares += Square(rank = square.rank-2, file = square.file-1) } catch (_: Exception) {}
        return listSquares.toList()
    }

    override fun getMoveVision(board: BoardState, square: Square): List<Move> {
        val listSquares = getVision(square)
        var listOfMoves : Array<Move> = arrayOf()
        for(sqr in listSquares) {
            if(board.getPieceAt(sqr) == null) {
                listOfMoves += Move(square, sqr)
            }
        }
        return listOfMoves.toList()
    }

    override fun getCaptureVision(board: BoardState, square: Square): List<Move> {
        val listSquares = getVision(square)
        var listOfMoves : Array<Move> = arrayOf()
        for(sqr in listSquares) {
            if(board.getPieceAt(sqr) != null) {
                if(board.getPieceAt(sqr)!!.owner != owner) {
                    listOfMoves += Move(square, sqr)
                }
            }
        }
        return listOfMoves.toList()
    }

    override val fenLetterLowercase = 'n'
}
