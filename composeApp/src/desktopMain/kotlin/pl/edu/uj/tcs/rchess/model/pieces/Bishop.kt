package pl.edu.uj.tcs.rchess.model.pieces
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

class Bishop(owner: PlayerColor): Piece(owner = owner) {
    override fun getMoveVision(board: BoardState, square: Square): List<Move> {
        var listOfMoves : Array<Move> = arrayOf()
        for(i in 1..7) {
            try {
                if(board.getPieceAt(Square(square.rank + i, square.file + i)) == null) {
                    listOfMoves += Move(square, Square(square.rank+i, square.file+i))
                } else {
                    break
                }
            } catch(_: Exception) { break }
        }
        for(i in 1..7) {
            try {
                if(board.getPieceAt(Square(square.rank + i, square.file - i)) == null) {
                    listOfMoves += Move(square, Square(square.rank+i, square.file-i))
                } else {
                    break
                }
            } catch(_: Exception) { break }
        }
        for(i in 1..7) {
            try {
                if(board.getPieceAt(Square(square.rank - i, square.file + i)) == null) {
                    listOfMoves += Move(square, Square(square.rank-i, square.file+i))
                } else {
                    break
                }
            } catch(_: Exception) { break }
        }
        for(i in 1..7) {
            try {
                if(board.getPieceAt(Square(square.rank - i, square.file - i)) == null) {
                    listOfMoves += Move(square, Square(square.rank-i, square.file-i))
                } else {
                    break
                }
            } catch(_: Exception) { break }
        }
        return listOfMoves.toList()
    }

    override fun getCaptureVision(board: BoardState, square: Square): List<Move> {
        var listOfMoves : Array<Move> = arrayOf()
        for(i in 1..7) {
            try {
                if(board.getPieceAt(Square(square.rank + i, square.file + i)) == null) {
                    continue
                } else {
                    if(board.getPieceAt(Square(square.rank + i, square.file + i))!!.owner != owner) {
                        listOfMoves += Move(square, Square(square.rank + i, square.file + i))
                    }
                }
            } catch(_: Exception) { break }
        }
        for(i in 1..7) {
            try {
                if(board.getPieceAt(Square(square.rank + i, square.file - i)) == null) {
                    continue
                } else {
                    if(board.getPieceAt(Square(square.rank + i, square.file - i))!!.owner != owner) {
                        listOfMoves += Move(square, Square(square.rank + i, square.file - i))
                    }
                }
            } catch(_: Exception) { break }
        }
        for(i in 1..7) {
            try {
                if(board.getPieceAt(Square(square.rank - i, square.file + i)) == null) {
                    continue
                } else {
                    if(board.getPieceAt(Square(square.rank - i, square.file + i))!!.owner != owner) {
                        listOfMoves += Move(square, Square(square.rank - i, square.file + i))
                    }
                }
            } catch(_: Exception) { break }
        }
        for(i in 1..7) {
            try {
                if(board.getPieceAt(Square(square.rank - i, square.file - i)) == null) {
                    continue
                } else {
                    if(board.getPieceAt(Square(square.rank - i, square.file - i))!!.owner != owner) {
                        listOfMoves += Move(square, Square(square.rank - i, square.file - i))
                    }
                }
            } catch(_: Exception) { break }
        }
        return listOfMoves.toList()
    }

    override val fenLetterLowercase = 'b'
}
