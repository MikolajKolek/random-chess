package pl.edu.uj.tcs.rchess.model.pieces
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

class Pawn(owner: PlayerColor): Piece(owner = owner) {
    override fun getMoveVision(board: BoardState, square: Square): List<Move> {
        val validSquares = mutableListOf<Square>()

        if(owner == PlayerColor.WHITE) {
            if(board.getPieceAt(square + Square.Vector(1, 0)) == null) {
                validSquares += (square + Square.Vector(1, 0))!!

                if(square.rank == 1 && board.getPieceAt(square + Square.Vector(2, 0)) == null)
                    validSquares += (square + Square.Vector(2, 0))!!
            }
        }
        else {
            if(board.getPieceAt(square + Square.Vector(-1, 0)) == null) {
                validSquares += (square + Square.Vector(-1, 0))!!

                if(square.rank == 6 && board.getPieceAt(square + Square.Vector(-2, 0)) == null)
                    validSquares += (square + Square.Vector(-2, 0))!!
            }
        }

        return validSquaresToMoves(validSquares, square)
    }

    override fun getCaptureVision(board: BoardState, square: Square): List<Move> {
        val possibleSquares = if(owner == PlayerColor.WHITE) listOf(
            square + Square.Vector(1, 1),
            square + Square.Vector(1, -1),
        ) else listOf(
            square + Square.Vector(-1, 1),
            square + Square.Vector(-1, -1),
        )

        val validSquares = mutableListOf<Square>()
        for(sqr in possibleSquares.filterNotNull()) {
            if(sqr == board.enPassantTarget) {
                if(owner == PlayerColor.WHITE && sqr.rank == 5)
                    validSquares += sqr
                else if(owner == PlayerColor.BLACK && sqr.rank == 2)
                    validSquares += sqr
            }

            if(board.getPieceAt(sqr)?.owner != owner)
                validSquares += sqr
        }

        return validSquaresToMoves(validSquares.toList(), square)
    }

    fun validSquaresToMoves(squares: List<Square>, startingSquare: Square) =
        squares.map {
            if((it.rank == 7 && owner == PlayerColor.WHITE) || (it.rank == 0 && owner == PlayerColor.BLACK)) {
                setOf(
                    Move(startingSquare, it, Move.Promotion.KNIGHT),
                    Move(startingSquare, it, Move.Promotion.BISHOP),
                    Move(startingSquare, it, Move.Promotion.ROOK),
                    Move(startingSquare, it, Move.Promotion.QUEEN)
                )
            }
            else
                setOf(Move(startingSquare, it))
        }.flatten()

    override val fenLetterLowercase = 'p'
}
