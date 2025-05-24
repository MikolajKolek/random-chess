package pl.edu.uj.tcs.rchess.model.pieces

import pl.edu.uj.tcs.rchess.model.state.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

class Pawn(owner: PlayerColor) : Piece(owner = owner) {
    val advanceVector: Square.Vector = when (owner) {
        PlayerColor.WHITE -> Square.Vector(1, 0)
        PlayerColor.BLACK -> Square.Vector(-1, 0)
    }

    val captureVectors: List<Square.Vector> = listOf(
        advanceVector + Square.Vector(0, -1),
        advanceVector + Square.Vector(0, 1),
    )

    override fun getMoveVision(boardState: BoardState, square: Square): List<Move> {
        val validSquares = mutableListOf<Square>()

        (square + advanceVector)
            ?.takeIf { boardState.board[it] == null }
            ?.let { advanceSquare ->
                validSquares += advanceSquare

                (advanceSquare + advanceVector)
                    ?.takeIf { square.rank == owner.pawnDoubleMoveRank && boardState.board[it] == null }
                    ?.let { doubleMoveSquare ->
                        validSquares += doubleMoveSquare
                    }
                }

        return validSquaresToMoves(validSquares, square)
    }

    override fun getCaptureVision(boardState: BoardState, square: Square): List<Move> {
        val possibleSquares = captureVectors.mapNotNull { square + it }

        val validSquares = mutableListOf<Square>()
        for (sqr in possibleSquares) {
            if (sqr == boardState.enPassantTarget && sqr.rank == owner.enPassantTargetRank)
                validSquares += sqr

            if (boardState.board[sqr]?.owner == owner.opponent)
                validSquares += sqr
        }

        return validSquaresToMoves(validSquares.toList(), square)
    }

    fun validSquaresToMoves(squares: List<Square>, startingSquare: Square) =
        squares.map {
            if (it.rank == owner.promotionRank) {
                setOf(
                    Move(startingSquare, it, Move.Promotion.KNIGHT),
                    Move(startingSquare, it, Move.Promotion.BISHOP),
                    Move(startingSquare, it, Move.Promotion.ROOK),
                    Move(startingSquare, it, Move.Promotion.QUEEN)
                )
            } else
                setOf(Move(startingSquare, it))
        }.flatten()

    override val fenLetterLowercase = 'p'

    override val unicodeSymbol = when (owner) {
        PlayerColor.WHITE -> "♙"
        PlayerColor.BLACK -> "♟"
    }
}
