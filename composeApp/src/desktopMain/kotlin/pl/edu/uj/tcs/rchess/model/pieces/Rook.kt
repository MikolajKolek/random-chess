package pl.edu.uj.tcs.rchess.model.pieces
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

class Rook(owner: PlayerColor): Piece(owner = owner) {
    override fun getMoveVision(board: BoardState, square: Square): List<Move> {
        var listOfMoves : Array<Move> = arrayOf()
        for(i in 1..7) {
            try {
                if(board.getPieceAt(square.copy(rank = square.rank + i)) == null) {
                    listOfMoves += Move(square, square.copy(rank = square.rank+i))
                } else {
                    break
                }
            } catch(_: Exception) { break }
        }
        for(i in 1..7) {
            try {
                if(board.getPieceAt(square.copy(rank = square.rank - i)) == null) {
                    listOfMoves += Move(square, square.copy(rank = square.rank-i))
                } else {
                    break
                }
            } catch(_: Exception) { break }
        }
        for(i in 1..7) {
            try {
                if(board.getPieceAt(square.copy(file = square.file + i)) == null) {
                    listOfMoves += Move(square, square.copy(file = square.file+i))
                } else {
                    break
                }
            } catch(_: Exception) { break }
        }
        for(i in 1..7) {
            try {
                if(board.getPieceAt(square.copy(file = square.file - i)) == null) {
                    listOfMoves += Move(square, square.copy(file = square.file-i))
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
                if(board.getPieceAt(square.copy(rank = square.rank + i)) == null) {
                    continue
                } else {
                    if(board.getPieceAt(square.copy(rank = square.rank + i))!!.owner != owner) {
                        listOfMoves += Move(square, square.copy(rank = square.rank + i))
                    }
                }
            } catch(_: Exception) { break }
        }
        for(i in 1..7) {
            try {
                if(board.getPieceAt(square.copy(rank = square.rank - i)) == null) {
                    continue
                } else {
                    if(board.getPieceAt(square.copy(rank = square.rank - i))!!.owner != owner) {
                        listOfMoves += Move(square, square.copy(rank = square.rank - i))
                    }
                }
            } catch(_: Exception) { break }
        }
        for(i in 1..7) {
            try {
                if(board.getPieceAt(square.copy(file = square.file + i)) == null) {
                    continue
                } else {
                    if(board.getPieceAt(square.copy(file = square.file + i))!!.owner != owner) {
                        listOfMoves += Move(square, square.copy(file = square.file + i))
                    }
                }
            } catch(_: Exception) { break }
        }
        for(i in 1..7) {
            try {
                if(board.getPieceAt(square.copy(file = square.file - i)) == null) {
                    continue
                } else {
                    if(board.getPieceAt(square.copy(file = square.file - i))!!.owner != owner) {
                        listOfMoves += Move(square, square.copy(file = square.file - i))
                    }
                }
            } catch(_: Exception) { break }
        }
        return listOfMoves.toList()
    }

    override val fenLetterLowercase = 'r'
}
