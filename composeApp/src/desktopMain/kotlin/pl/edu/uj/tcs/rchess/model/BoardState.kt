package pl.edu.uj.tcs.rchess.model

import pl.edu.uj.tcs.rchess.model.pieces.King
import pl.edu.uj.tcs.rchess.model.pieces.Pawn
import pl.edu.uj.tcs.rchess.model.pieces.Piece
import pl.edu.uj.tcs.rchess.model.pieces.Rook

/**
 * Immutable class describing the state of the chessboard.
 *
 * Can be exactly serialized to/from FEN.
 */
class BoardState(
    private val board: List<Piece?>,
    val currentTurn: PlayerColor,
    val enPassantTarget: Square?,
    val castlingRights: CastlingRights,
    val halfmoveCounter: Int,
    val fullmoveNumber: Int
) {
    init {
        require(board.size == 64) { "Board must have 64 squares." }
    }

    companion object {
        fun empty() = BoardState(
            board = List(64) { null },
            currentTurn = PlayerColor.WHITE,
            enPassantTarget = null,
            castlingRights = CastlingRights.full(),
            halfmoveCounter = 0,
            fullmoveNumber = 1
        )

        fun fromFen(fen: FEN): BoardState {
            TODO()
        }
    }

    /**
     * @param square The square to check
     * @return The piece on the given square (or null if there is none).
     */
    fun getPieceAt(square: Square) = board[square.row * 8 + square.col]

    /**
     * @param move The move to apply.
     * @throws IllegalStateException when the current position is not legal.
     * @throws IllegalArgumentException when the given move is not valid.
     * @return The current BoardState.
     */
    fun applyMove(move: Move) : BoardState {
        if(!isLegal()) throw IllegalStateException( "Cannot apply move to illegal position." )
        require(isValidMove(move)) { "Invalid move given." }

        val newBoard : Array<Piece?> = board.toTypedArray()
        var newEnPassant : Square? = null
        var newHalfMoveCounter = halfmoveCounter+1

        // Set en passant in new board
        if(getPieceAt(move.from) is Pawn) {
            newHalfMoveCounter = 0
            if(Math.abs(move.from.row - move.to.row) == 2) {
                newEnPassant = Square((move.from.row + move.to.row)/2, move.from.col)
            }
        }

        // Set proper castling rights in new board
        val castlingArray = arrayOf(
            castlingRights.whiteKingSide,
            castlingRights.whiteQueenSide,
            castlingRights.blackKingSide,
            castlingRights.blackQueenSide,
        )
        if(getPieceAt(move.from) is King) {
            if(getPieceAt(move.from)?.owner == PlayerColor.WHITE) {
                castlingArray[0] = false
                castlingArray[1] = false
            } else {
                castlingArray[2] = false
                castlingArray[3] = false
            }
        } else if(getPieceAt(move.from) is Rook) {
            if(move.from.col == 1) {
                if(move.from.row == 1) {
                    castlingArray[1] = false //A1
                } else if(move.from.row == 8) {
                    castlingArray[3] = false //A8
                }
            } else if(move.from.col == 8) {
                if(move.from.row == 1) {
                    castlingArray[0] = false //H1
                } else if(move.from.row == 8) {
                    castlingArray[2] = false //H8
                }
            }
        }



        // Perform the move on new board
        if(getPieceAt(move.to) != null) newHalfMoveCounter = 0
        newBoard[move.to.row * 8 + move.to.col] = newBoard[move.from.row * 8 + move.from.col]
        newBoard[move.from.row * 8 + move.from.col] = null

        return BoardState(
            newBoard.toList(),
            currentTurn.getOpponent(),
            newEnPassant,
            CastlingRights.fromArray(castlingArray),
            newHalfMoveCounter,
            fullmoveNumber+1
        )
    }

    /**
     * @param move The move to check.
     * @return True if the given move is valid in this context, otherwise false.
     */
    private fun isValidMove(move: Move): Boolean {
        TODO()
    }

    /**
     * @return Returns true if the current position is legal
     */
    fun isLegal() : Boolean {
        TODO()
    }

    /**
     * @param move The move to verify.
     * @return Returns true if the given move is legal in this position.
     */
    fun isLegalMove(move : Move) = applyMove(move).isLegal()

    /**
     * @return List of legal moves for the piece at the given position.
     * If there is no piece at the position, return an empty list.
     */
    fun getLegalMovesFor(square: Square) = getPieceAt(square)?.getLegalMoves(this, square) ?: emptyList()

    /**
     * @return The FEN representation of this GameState.
     * @see FEN
     */
    fun toFen(): String {
        TODO()
    }

    /**
     * @return The internal reason of game over (or null, if there is none)
     * @see GameOverReason
     */
    fun isOver() : GameOverReason? {
        TODO()
    }
}
// TODO: Implement the logic managing BoardState
