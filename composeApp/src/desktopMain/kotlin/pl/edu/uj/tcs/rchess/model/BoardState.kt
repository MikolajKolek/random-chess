package pl.edu.uj.tcs.rchess.model

import pl.edu.uj.tcs.rchess.model.Fen.Companion.fromFen
import pl.edu.uj.tcs.rchess.model.board.Board
import pl.edu.uj.tcs.rchess.model.board.emptyBoard
import pl.edu.uj.tcs.rchess.model.pieces.*
import kotlin.math.abs
import kotlin.reflect.KClass

/**
 * Immutable class describing the state of the chessboard.
 *
 * Can be exactly serialized to/from FEN.
 */
class BoardState(
    val board: Board,
    val currentTurn: PlayerColor,
    val enPassantTarget: Square?,
    val castlingRights: CastlingRights,
    val halfmoveCounter: Int,
    val fullmoveNumber: Int
) {
    init {
        require(halfmoveCounter >= 0) { "Halfmove counter must not be negative." }
        require(fullmoveNumber >= 1) { "Fullmove number must be positive." }
    }

    companion object {
        fun empty() = BoardState(
            board = emptyBoard(),
            currentTurn = PlayerColor.WHITE,
            enPassantTarget = null,
            castlingRights = CastlingRights.full(),
            halfmoveCounter = 0,
            fullmoveNumber = 1
        )

        fun initial() = fromFen(Fen.INITIAL)
    }

    /**
     * @param move The move to apply.
     * @throws IllegalStateException when the current position is not legal.
     * @throws IllegalArgumentException when the given move is not valid.
     * @return The new BoardState.
     */
    fun applyMove(move: Move) : BoardState {
        if(!isLegal()) throw IllegalStateException( "Cannot apply move to illegal position." )

        val pieceFrom = fromPieceIfMoveValid(move) ?: throw IllegalArgumentException("Invalid move given.")
        val pieceTo = board[move.to]

        val newBoard = board.toMutableBoard()
        var newEnPassant : Square? = null
        var newHalfMoveCounter = halfmoveCounter + 1
        var newCastlingRights: CastlingRights = castlingRights

        // Set en passant in the new board
        if(pieceFrom is Pawn) {
            newHalfMoveCounter = 0

            if(abs(move.from.rank - move.to.rank) == 2)
                newEnPassant = Square((move.from.rank + move.to.rank) / 2, move.from.file)

            if(move.to == enPassantTarget) {
                if(enPassantTarget.rank == 2)
                    newBoard[move.to.copy(rank = 3)] = null
                else
                    newBoard[move.to.copy(rank = 4)] = null
            }
        }

        // Set proper castling rights in the new board
        if(pieceFrom is King) {
            newCastlingRights = newCastlingRights.withoutBoth(pieceFrom.owner)

            if(abs(move.from.file - move.to.file) == 2) {
                when(move.to) {
                    Square(0, 2) -> {
                        newBoard[Square(0, 3)] = Rook(PlayerColor.WHITE)
                        newBoard[Square(0, 0)] = null
                    }
                    Square(0, 6) -> {
                        newBoard[Square(0, 5)] = Rook(PlayerColor.WHITE)
                        newBoard[Square(0, 7)] = null
                    }
                    Square(7, 2) -> {
                        newBoard[Square(7, 3)] = Rook(PlayerColor.BLACK)
                        newBoard[Square(7, 0)] = null
                    }
                    Square(7, 6) -> {
                        newBoard[Square(7, 5)] = Rook(PlayerColor.BLACK)
                        newBoard[Square(7, 7)] = null
                    }
                    else -> throw IllegalArgumentException("Invalid castling move.")
                }
            }
        } else if(pieceFrom is Rook) {
            if(move.from.file == 0) {
                if(move.from.rank == 0) {
                    newCastlingRights = newCastlingRights.copy(whiteQueenSide = false)
                } else if(move.from.rank == 7) {
                    newCastlingRights = newCastlingRights.copy(blackQueenSide = false)
                }
            } else if(move.from.file == 7) {
                if(move.from.rank == 0) {
                    newCastlingRights = newCastlingRights.copy(whiteKingSide = false)
                } else if(move.from.rank == 7) {
                    newCastlingRights = newCastlingRights.copy(blackKingSide = false)
                }
            }
        }

        if(move.to == Square.fromString("a1"))
            newCastlingRights = castlingRights.copy(whiteQueenSide = false)
        if(move.to == Square.fromString("a8"))
            newCastlingRights = castlingRights.copy(blackQueenSide = false)
        if(move.to == Square.fromString("h1"))
            newCastlingRights = newCastlingRights.copy(whiteKingSide = false)
        if(move.to == Square.fromString("h8"))
            newCastlingRights = newCastlingRights.copy(blackKingSide = false)

        if(pieceTo != null)
            newHalfMoveCounter = 0

        // Perform the move on the new board
        newBoard[move.from] = null
        newBoard[move.to] = when (move.promoteTo) {
            null -> board[move.from]
            Move.Promotion.KNIGHT -> Knight(pieceFrom.owner)
            Move.Promotion.BISHOP -> Bishop(pieceFrom.owner)
            Move.Promotion.ROOK -> Rook(pieceFrom.owner)
            Move.Promotion.QUEEN -> Queen(pieceFrom.owner)
        }

        return BoardState(
            board = newBoard,
            currentTurn.opponent,
            newEnPassant,
            newCastlingRights,
            newHalfMoveCounter,
            if(currentTurn == PlayerColor.BLACK) fullmoveNumber + 1 else fullmoveNumber
        )
    }

    /**
     * @param move The move to check.
     * @return Piece at the [move.from] square if the move is valid, null otherwise.
     */
    private fun fromPieceIfMoveValid(move: Move): Piece? =
        board[move.from]?.takeIf { piece ->
            piece.owner == currentTurn && piece.getPieceVision(this, move.from).contains(move)
        }

    /**
     * @param move The move to check.
     * @return True if the given move is valid in this context, otherwise false.
     */
    private fun isValidMove(move: Move) = fromPieceIfMoveValid(move) != null

    /**
     * @return Returns true if the current position is legal
     */
    fun isLegal() : Boolean {
        // Both kings must be on the board
        // King of player not on move must not be in check
        locateKing(currentTurn)
        if(isInCheck(currentTurn.opponent)) return false

        // Pawns must not be on ranks 1 and 8
        for(f in 0..7) {
            if (board[Square(0, f)] is Pawn)
                return false
            if (board[Square(7, f)] is Pawn)
                return false
        }

        return true
    }

    fun locateKing(player: PlayerColor) : Square {
        var kingSquare : Square? = null
        for((square, piece) in squaresToNotNullPieces()) {
            if(piece is King && piece.owner == player) {
                require(kingSquare == null) { "There must not be more than one king of each color." }
                kingSquare = square
            }
        }

        require(kingSquare != null) { "There must be a king of each color." }
        return kingSquare
    }

    /**
     * @param player The player to verify
     * @return Returns true if the given player is in check, false otherwise
     */
    fun isInCheck(player: PlayerColor) : Boolean {
        val kingSquare = locateKing(player)

        for((square, piece) in squaresToNotNullPieces()) {
            if(piece.owner == player)
                continue

            for(move in piece.getPieceVision(this, square))
                if(move.to == kingSquare)
                    return true
        }

        return false
    }

    /**
     * @param move The move to verify.
     * @return True if the given move is legal in this position.
     */
    fun isLegalMove(move : Move) : Boolean {
        if(board[move.from] is King) {
            if(abs(move.from.file - move.to.file) == 2) {
                if(isInCheck(currentTurn)) return false
                val halfCastle = move.copy(to = move.to.copy(file = (move.to.file+move.from.file)/2))
                if(!applyMove(halfCastle).isLegal()) return false
            }
        }
        return applyMove(move).isLegal()
    }

    /**
     * @return List of legal moves for the piece at the given position.
     * If there is no piece at the position, return an empty list.
     */
    fun getLegalMovesFor(square: Square) = board[square]
        ?.let { piece -> if(piece.owner == currentTurn) piece else null }
        ?.getLegalMoves(this, square) ?: emptyList()

    /**
     * @param player The player to check.
     * @return True if the given player has any piece other than their king.
     */
    fun hasAnyNonKingMaterial(player: PlayerColor) : Boolean {
        for(piece in board.notNullPieces())
            if(piece !is King && piece.owner == player)
                return true

        return false
    }

    /**
     * @return The internal reason of game over (or null, if there is none)
     * @see GameOverReason
     */
    fun isOver() : GameOverReason? {
        // This method only checks game over reasons within the single BoardState
        // That is checkmate, stalemate, insufficient material and 50 move rule
        val allLegalMoves = mutableListOf<Move>()
        for(r in 0..7) {
            for(f in 0..7) {
                if(board.get(Square(r, f))?.owner == currentTurn) {
                    allLegalMoves.addAll(getLegalMovesFor(Square(r, f)))
                }
            }
        }
        if(allLegalMoves.isEmpty()) {
            return if(isInCheck(currentTurn))
                GameOverReason.CHECKMATE
            else
                GameOverReason.STALEMATE
        }

        if(halfmoveCounter >= 100)
            return GameOverReason.FIFTY_MOVE_RULE

        var whiteLight = 0
        var blackLight = 0
        for(piece in board.notNullPieces()) {
            if(piece is King)
                continue

            if(piece is Bishop || piece is Knight) {
                if(piece.owner == PlayerColor.WHITE)
                    whiteLight++
                else
                    blackLight++
            }
            else
                return null
        }

        if((whiteLight == 0 && blackLight <= 1) || (whiteLight <= 1 && blackLight == 0))
            return GameOverReason.INSUFFICIENT_MATERIAL

        return null
    }

    private fun squaresToNotNullPieces() =
        (0..7).map { rank ->
            (0..7).associate { file ->
                val square = Square(rank, file)
                square to board[square]
            }
        }.flatMap { it.entries }.associate { it.toPair() }.filterValues { it != null }.mapValues { it.value!! }

    fun applyStandardAlgebraicMove(move: String) =
        applyMove(standardAlgebraicToMove(move))

    fun standardAlgebraicToMove(sa : String) : Move {
        var _sa = sa
        var requiresCheckmate = false
        var requiresCheck = false
        var requiresCapture = false
        var fileDisambiguation : Int? = null
        var rankDisambiguation : Int? = null
        val move : Move

        if(_sa.last() == '#') {
            _sa = _sa.dropLast(1)
            requiresCheckmate = true
        }
        if(_sa.last() == '+') {
            require(!requiresCheckmate) { "A move cannot be check and checkmate at the same time." }
            _sa = _sa.dropLast(1)
            requiresCheck = true
        }

        // Castling
        if(_sa.contains("O-O-O")) {
            require(_sa == "O-O-O") { "Invalid move pattern." }
            require(if(currentTurn==PlayerColor.WHITE) {castlingRights.whiteQueenSide} else {castlingRights.blackQueenSide}) { "Castling rights invalid." }
            if(currentTurn==PlayerColor.WHITE) {move = Move(Square.fromString("e1"),Square.fromString("c1"))}
            else {move = Move(Square.fromString("e8"), Square.fromString("c8"))}
            verifyCheckmate(move)
            verifyCheck(move)
            require(isLegalMove(move)) { "Castling is illegal here." }
            return move
        }

        if(_sa.contains("O-O")) {
            require(_sa == "O-O") { "Invalid move pattern."}
            require(if(currentTurn==PlayerColor.WHITE) {castlingRights.whiteKingSide} else {castlingRights.blackKingSide}) { "Castling rights invalid." }
            if(currentTurn==PlayerColor.WHITE) {move = Move(Square.fromString("e1"),Square.fromString("g1"))}
            else {move = Move(Square.fromString("e8"), Square.fromString("g8"))}
            verifyCheckmate(move)
            verifyCheck(move)
            require(isLegalMove(move)) { "Castling is illegal here." }
            return move
        }

        // Promotion
        require(_sa.length >= 2) { "Missing target square information." }
        var promotionPiece : Move.Promotion? = null
        if(_sa[_sa.length-2] == '=') {
            promotionPiece = Move.Promotion.fromIdentifier(_sa.last().lowercaseChar())
            _sa = _sa.dropLast(2)
        }

        // Target square
        require(_sa.length >= 2) { "Missing target square information." }
        val destinationSquare = Square.fromString(_sa.takeLast(2))
        _sa = _sa.dropLast(2)

        // Capture
        if(_sa.isNotEmpty()) {
            if(_sa.last() == 'x') {
                requiresCapture = true
                _sa = _sa.dropLast(1)
            }
        }
        require(requiresCapture || board[destinationSquare] == null) { "No capture declared, but target square occupied." }

        // File and rank disambiguation
        // Redundant disambiguation is allowed here
        if(_sa.isNotEmpty()) {
            if(_sa.last().isDigit()) {
                rankDisambiguation = _sa.last().digitToInt()-1
                _sa = _sa.dropLast(1)
                require(rankDisambiguation in 0..7) { "Invalid rank." }
            }
        }
        if(_sa.isNotEmpty()) {
            if(_sa.last().isLowerCase()) {
                fileDisambiguation = _sa.last()-'a'
                _sa = _sa.dropLast(1)
                require(fileDisambiguation in 0..7) { "Invalid file." }
            }
        }

        // Piece performing the move
        val requiredPiece : KClass<out Piece>
        if(_sa.isNotEmpty()) {
            requiredPiece = Piece.fromFenLetter(_sa.last())::class
            _sa = _sa.dropLast(1)
        } else {
            requiredPiece = Pawn(currentTurn)::class
        }

        // No additional invalid characters
        require(_sa.isEmpty()) { "Unidentified characters present." }

        // Finding the correct move
        var returnMove : Move? = null
        for(r in 0..7) {
            for(f in 0..7) {
                val fromSquare = Square(rank = r, file = f)
                val piece = board[fromSquare] ?: continue
                if(piece.owner != currentTurn) continue
                if(piece::class != requiredPiece) continue
                if(rankDisambiguation != null) if(rankDisambiguation != r) continue
                if(fileDisambiguation != null) if(fileDisambiguation != f) continue
                val myMove = Move(fromSquare, destinationSquare, promotionPiece)
                if(!piece.getPieceVision(this, fromSquare).contains(myMove)) continue
                if(!applyMove(myMove).isLegal()) continue
                require(returnMove == null) { "Move definition ambiguous." }
                returnMove = myMove
            }
        }
        require(returnMove != null) { "No such move found to $destinationSquare" }
        require(requiresCheckmate == verifyCheckmate(returnMove)) { "Checkmate declaration mismatch." }
        require(requiresCheck || requiresCheckmate == verifyCheck(returnMove)) { "Check declaration mismatch." }
        if(requiresCapture) require(board[returnMove.from]!!.getCaptureVision(this, returnMove.from).contains(returnMove))
            { "Capture declared, but there is no piece to capture." }
        return returnMove
    }

    /**
     * @param move Move to verify.
     * @return True if the given move is checkmate, false otherwise.
     */
    private fun verifyCheckmate(move: Move) = (applyMove(move).isOver() == GameOverReason.CHECKMATE)

    /**
     * @param move Move to verify.
     * @return True if the given move is check, false otherwise.
     */
    private fun verifyCheck(move: Move) = applyMove(move).isInCheck(currentTurn.opponent)
}
