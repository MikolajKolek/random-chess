package pl.edu.uj.tcs.rchess.model

import pl.edu.uj.tcs.rchess.model.pieces.*

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
        require(halfmoveCounter >= 0) { "Halfmove counter must not be negative." }
        require(fullmoveNumber >= 1) { "Fullmove number must be positive." }
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
    fun getPieceAt(square: Square) = board[square.rank * 8 + square.file]

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
        var newCastlingRights: CastlingRights = castlingRights.copy()

        // Set en passant in new board
        if(getPieceAt(move.from) is Pawn) {
            newHalfMoveCounter = 0
            if(Math.abs(move.from.rank - move.to.rank) == 2) {
                newEnPassant = Square((move.from.rank + move.to.rank)/2, move.from.file)
            }
        }

        // Set proper castling rights in new board
        if(getPieceAt(move.from) is King) {
            if(getPieceAt(move.from)?.owner == PlayerColor.WHITE) {
                newCastlingRights = castlingRights.copy(whiteKingSide = false, whiteQueenSide = false)
            } else {
                newCastlingRights = castlingRights.copy(blackKingSide = false, blackQueenSide = false)
            }
        } else if(getPieceAt(move.from) is Rook) {
            if(move.from.file == 1) {
                if(move.from.rank == 1) {
                    newCastlingRights = castlingRights.copy(whiteQueenSide = false)
                } else if(move.from.rank == 8) {
                    newCastlingRights = castlingRights.copy(blackQueenSide = false)
                }
            } else if(move.from.file == 8) {
                if(move.from.rank == 1) {
                    newCastlingRights = castlingRights.copy(whiteKingSide = false)
                } else if(move.from.rank == 8) {
                    newCastlingRights = castlingRights.copy(whiteKingSide = false)
                }
            }
        }

        // Perform the move on new board
        if(getPieceAt(move.to) != null) newHalfMoveCounter = 0
        newBoard[move.to.rank * 8 + move.to.file] = newBoard[move.from.rank * 8 + move.from.file]
        newBoard[move.from.rank * 8 + move.from.file] = null
        if(move.promoteTo == Move.Promotion.KNIGHT) newBoard[move.to.rank * 8 + move.to.file] = Knight(newBoard[move.to.rank * 8 + move.to.file]?.owner ?: PlayerColor.WHITE)
        if(move.promoteTo == Move.Promotion.BISHOP) newBoard[move.to.rank * 8 + move.to.file] = Bishop(newBoard[move.to.rank * 8 + move.to.file]?.owner ?: PlayerColor.WHITE)
        if(move.promoteTo == Move.Promotion.ROOK) newBoard[move.to.rank * 8 + move.to.file] = Rook(newBoard[move.to.rank * 8 + move.to.file]?.owner ?: PlayerColor.WHITE)
        if(move.promoteTo == Move.Promotion.QUEEN) newBoard[move.to.rank * 8 + move.to.file] = Queen(newBoard[move.to.rank * 8 + move.to.file]?.owner ?: PlayerColor.WHITE)

        return BoardState(
            newBoard.toList(),
            currentTurn.getOpponent(),
            newEnPassant,
            newCastlingRights,
            newHalfMoveCounter,
            if(currentTurn == PlayerColor.BLACK) {fullmoveNumber+1} else {fullmoveNumber}
        )
    }

    /**
     * @param move The move to check.
     * @return True if the given move is valid in this context, otherwise false.
     */
    private fun isValidMove(move: Move): Boolean {
        if(getPieceAt(move.from) == null) return false //There must be a piece to move.
        if(getPieceAt(move.to) != null) {
            if(getPieceAt(move.to)!!.owner == getPieceAt(move.from)!!.owner) return false //Cannot capture your own pieces.
        }
        if(!getPieceAt(move.from)!!.getPieceVision(this, move.from).contains(move)) return false //Move must be valid for that piece.
        return true
    }

    /**
     * @return Returns true if the current position is legal
     */
    fun isLegal() : Boolean {
        // Both kings must be on the board
        // King of player not on move must not be in check
        isInCheck(currentTurn)
        if(isInCheck(currentTurn.getOpponent())) return false

        // Pawns must not be on ranks 1 and 8
        for(f in 0..7) {
            if(getPieceAt(Square(0, f)) is Pawn) return false
            if(getPieceAt(Square(7, f)) is Pawn) return false
        }

        return true
    }

    fun isInCheck(player: PlayerColor) : Boolean {
        var kingSquare : Square? = null
        for(r in 0..7) {
            for(f in 0..7) {
                if(getPieceAt(Square(r, f)) is King) {
                    if(getPieceAt(Square(r, f))!!.owner == player) {
                        require(kingSquare == null) { "There must not be more than one king of each color." }
                        kingSquare = Square(r, f)
                    }
                }
            }
        }
        require(kingSquare != null) { "There must be a king of each color." }
        for(r in 0..7) {
            for(f in 0..7) {
                if(getPieceAt(Square(r, f)) is King && getPieceAt(Square(r, f)) != null) {
                    if(getPieceAt(Square(r, f)) == null) continue
                    if(kingSquare == Square(r, f)) continue
                    if(getPieceAt(Square(r, f))!!.owner != getPieceAt(kingSquare)!!.owner) {
                        val captureList = getPieceAt(Square(r, f))!!.getCaptureVision(this, Square(r, f))
                        for(move in captureList) {
                            if(move.to == kingSquare) {
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
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
