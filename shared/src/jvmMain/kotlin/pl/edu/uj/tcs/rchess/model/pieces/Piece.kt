package pl.edu.uj.tcs.rchess.model.pieces

import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square
import pl.edu.uj.tcs.rchess.model.state.BoardState

/**
 * Sealed class describing all chess pieces.
 * @param owner The color of the piece.
 */
sealed class Piece(val owner: PlayerColor) {
    /**
     * @param boardState The board that this piece is on.
     * @param square The square that this piece is on.
     * @return List of all possible and legal moves that this piece can currently perform.
     */
    fun getLegalMoves(boardState: BoardState, square: Square): List<Move> {
        val piece = boardState.board[square]
            ?: throw IllegalArgumentException("The square is not occupied by any piece")
        require(piece::class == this::class) { "The square is not occupied by this piece" }
        require(piece.owner == owner) { "The piece in the given square has a different owner" }

        return getPieceVision(boardState, square).filter { boardState.isLegalMove(it) }
    }

    /**
     * @param boardState The board that this piece is on.
     * @param square The square that this piece is on.
     * @return List of all capturing moves.
     */
    abstract fun getCaptureVision(boardState: BoardState, square: Square): List<Move>

    /**
     * @param boardState The board that this piece is on.
     * @param square The square that this piece is on.
     * @return List of all moves without captures.
     */
    abstract fun getMoveVision(boardState: BoardState, square: Square): List<Move>

    /**
     * @param boardState The board that this piece is on.
     * @param square The square that this piece is on.
     * @return List of all valid moves this piece can make.
     * @see getCaptureVision
     * @see getMoveVision
     */
    fun getPieceVision(boardState: BoardState, square: Square) =
        getMoveVision(boardState, square) + getCaptureVision(boardState, square)

    /**
     * Lowercase letter representing the piece.
     */
    abstract val fenLetterLowercase: Char

    /**
     * Case-sensitive letter representing the piece as it appears in FEN notation.
     */
    val fenLetter: Char
        get() = when (owner) {
            PlayerColor.WHITE -> fenLetterLowercase.uppercaseChar()
            PlayerColor.BLACK -> fenLetterLowercase
        }

    /**
     * Unicode symbol representing the piece in the correct color.
     */
    abstract val unicodeSymbol: String

    companion object {
        /**
         * @param fenLetter The letter representing the piece as it appears in FEN notation.
         * @return The piece corresponding to the given letter and color.
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
