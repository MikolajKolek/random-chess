package pl.edu.uj.tcs.rchess.model.pieces

import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

/**
 * Sealed class describing all chess pieces.
 * @param owner The color of the piece.
 */
sealed class Piece(val owner: PlayerColor) {
    /**
     * @param board The board that this piece is on.
     * @param square The square that this piece is on.
     * @return List of all possible and legal moves that this piece can currently perform.
     */
    fun getLegalMoves(board: BoardState, square: Square): List<Move> {
        val piece = board.getPieceAt(square)
            ?: throw IllegalArgumentException("The square is not occupied by any piece")
        require(piece::class == this::class) { "The square is not occupied by this piece" }
        require(piece.owner == owner) { "The piece in the given square has a different owner" }

        return getPieceVision(board, square).filter { board.isLegalMove(it) }
    }

    /**
     * @param board The board that this piece is on.
     * @param square The square that this piece is on.
     * @return List of all capturing moves.
     */
    abstract fun getCaptureVision(board: BoardState, square: Square): List<Move>

    /**
     * @param board The board that this piece is on.
     * @param square The square that this piece is on.
     * @return List of all moves without captures.
     */
    abstract fun getMoveVision(board: BoardState, square: Square): List<Move>

    /**
     * @param board The board that this piece is on.
     * @param square The square that this piece is on.
     * @return List of all valid moves this piece can make.
     * @see getCaptureVision
     * @see getMoveVision
     */
    fun getPieceVision(board: BoardState, square: Square) =
        getMoveVision(board, square) + getCaptureVision(board, square)

    /**
     * Lowercase letter representing the piece
     */
    abstract val fenLetterLowercase: Char

    /**
     * Case-sensitive letter representing the piece as it appears in FEN notation
     */
    val fenLetter: Char
        get() = when (owner) {
            PlayerColor.WHITE -> fenLetterLowercase.uppercaseChar()
            PlayerColor.BLACK -> fenLetterLowercase
        }

    companion object {
        /**
         * @param fenLetter The letter representing the piece as it appears in FEN notation
         * @return The piece corresponding to the given letter and color
         */
        fun fromFenLetter(fenLetter: Char): Piece {
            val owner = if(fenLetter.isUpperCase()) { PlayerColor.WHITE } else { PlayerColor.BLACK }

            return when (fenLetter.lowercaseChar()) {
                'k' -> King(owner)
                'q' -> Queen(owner)
                'r' -> Rook(owner)
                'b' -> Bishop(owner)
                'n' -> Knight(owner)
                'p' -> Pawn(owner)
                else -> throw IllegalArgumentException("Invalid fen piece letter: $fenLetter")
            }
        }
    }
}
