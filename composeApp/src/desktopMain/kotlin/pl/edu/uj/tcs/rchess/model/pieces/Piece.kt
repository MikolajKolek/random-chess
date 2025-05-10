package pl.edu.uj.tcs.rchess.model.pieces

import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

/**
 * Sealed class describing all chess pieces.
 * @param owner The color of the piece.
 */
sealed class Piece(
    /**
     * The color of the piece.
     */
    val owner: PlayerColor,
) {
    /**
     * @param board The board that this piece is on.
     * @param square The square that this piece is on.
     * @return List of all possible and legal moves that this piece can currently perform.
     */
    fun getLegalMoves(board: BoardState, square: Square): List<Move> {
        require(board.getPieceAt(square) != null) {}
        val moves = getPieceVision(board, square)
        val legalMoves : List<Move> = listOf()
        for(move in moves) {
            if(board.applyMove(move).isLegal()) {
                legalMoves.plus(move)
            }
        }
        return legalMoves
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
    fun getPieceVision(board: BoardState, square: Square) = getMoveVision(board, square).plus(getCaptureVision(board, square))

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
}
