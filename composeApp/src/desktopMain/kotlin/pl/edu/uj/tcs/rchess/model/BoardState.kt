package pl.edu.uj.tcs.rchess.model

import pl.edu.uj.tcs.rchess.model.pieces.*
import kotlin.math.abs
import kotlin.reflect.KClass

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
            val newBoard = MutableList<Piece?>(size = 64) { null }
            for(row in 7 downTo 0) {
                val fenRow = fen.boardState[7 - row]
                var column = 0

                for(v in fenRow) {
                    if(v.isDigit())
                        column += v.digitToInt()
                    else {
                        newBoard[(8 * row) + column] = Piece.fromFenLetter(v)
                        column++
                    }
                }
            }

            return BoardState(
                board = newBoard,
                currentTurn = if(fen.color == 'w') { PlayerColor.WHITE } else { PlayerColor.BLACK },
                castlingRights = CastlingRights.fromString(fen.castling),
                enPassantTarget = Square.fromStringOrNull(fen.enPassantSquare),
                halfmoveCounter = fen.halfmoveCounter,
                fullmoveNumber = fen.fullmoveNumber
            )
        }

        fun initial() = fromFen(FEN())
    }

    /**
     * @param square The square to check
     * @return The piece on the given square (or null if there is none).
     */
    fun getPieceAt(square: Square?) =
        if(square == null) null
        else board[(square.rank * 8) + square.file]

    /**
     * @param move The move to apply.
     * @throws IllegalStateException when the current position is not legal.
     * @throws IllegalArgumentException when the given move is not valid.
     * @return The new BoardState.
     */
    fun applyMove(move: Move) : BoardState {
        if(!isLegal()) throw IllegalStateException( "Cannot apply move to illegal position." )

        val pieceFrom = fromPieceIfMoveValid(move) ?: throw IllegalArgumentException("Invalid move given.")
        val pieceTo = getPieceAt(move.to)

        val newBoard = board.toMutableList()
        var newEnPassant : Square? = null
        var newHalfMoveCounter = halfmoveCounter + 1
        var newCastlingRights: CastlingRights = castlingRights.copy()

        // Set en passant in the new board
        if(pieceFrom is Pawn) {
            newHalfMoveCounter = 0

            if(abs(move.from.rank - move.to.rank) == 2)
                newEnPassant = Square((move.from.rank + move.to.rank) / 2, move.from.file)

            if(move.to == enPassantTarget) {
                if(enPassantTarget.rank == 2)
                    newBoard[move.to.copy(rank = 3).positionInBoard()] = null
                else
                    newBoard[move.to.copy(rank = 4).positionInBoard()] = null
            }
        }

        // Set proper castling rights in the new board
        if(pieceFrom is King) {
            newCastlingRights = if(pieceFrom.owner == PlayerColor.WHITE) {
                castlingRights.copy(whiteKingSide = false, whiteQueenSide = false)
            } else {
                castlingRights.copy(blackKingSide = false, blackQueenSide = false)
            }

            if(abs(move.from.file - move.to.file) == 2) {
                when(move.to) {
                    Square(0, 2) -> {
                        newBoard[Square(0, 3).positionInBoard()] = Rook(PlayerColor.WHITE)
                        newBoard[Square(0, 0).positionInBoard()] = null
                    }
                    Square(0, 6) -> {
                        newBoard[Square(0, 5).positionInBoard()] = Rook(PlayerColor.WHITE)
                        newBoard[Square(0, 7).positionInBoard()] = null
                    }
                    Square(7, 2) -> {
                        newBoard[Square(7, 3).positionInBoard()] = Rook(PlayerColor.BLACK)
                        newBoard[Square(7, 0).positionInBoard()] = null
                    }
                    Square(7, 6) -> {
                        newBoard[Square(7, 5).positionInBoard()] = Rook(PlayerColor.BLACK)
                        newBoard[Square(7, 7).positionInBoard()] = null
                    }
                    else -> throw IllegalArgumentException("Invalid castling move.")
                }
            }
        } else if(pieceFrom is Rook) {
            if(move.from.file == 0) {
                if(move.from.rank == 0) {
                    newCastlingRights = castlingRights.copy(whiteQueenSide = false)
                } else if(move.from.rank == 7) {
                    newCastlingRights = castlingRights.copy(blackQueenSide = false)
                }
            } else if(move.from.file == 7) {
                if(move.from.rank == 0) {
                    newCastlingRights = castlingRights.copy(whiteKingSide = false)
                } else if(move.from.rank == 7) {
                    newCastlingRights = castlingRights.copy(blackKingSide = false)
                }
            }
        }

        if(pieceTo != null)
            newHalfMoveCounter = 0

        // Perform the move on the new board
        newBoard[move.from.positionInBoard()] = null
        newBoard[move.to.positionInBoard()] = when (move.promoteTo) {
            null -> board[move.from.positionInBoard()]
            Move.Promotion.KNIGHT -> Knight(pieceFrom.owner)
            Move.Promotion.BISHOP -> Bishop(pieceFrom.owner)
            Move.Promotion.ROOK -> Rook(pieceFrom.owner)
            Move.Promotion.QUEEN -> Queen(pieceFrom.owner)
        }

        return BoardState(
            newBoard,
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
        getPieceAt(move.from)?.takeIf { piece ->
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
            if(getPieceAt(Square(0, f)) is Pawn)
                return false
            if(getPieceAt(Square(7, f)) is Pawn)
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
    fun isLegalMove(move : Move) = applyMove(move).isLegal()

    /**
     * @return List of legal moves for the piece at the given position.
     * If there is no piece at the position, return an empty list.
     */
    fun getLegalMovesFor(square: Square) = getPieceAt(square)?.getLegalMoves(this, square) ?: emptyList()

    /**
     * @param player The player to check.
     * @return True if the given player has any piece other than their king.
     */
    fun hasAnyNonKingMaterial(player: PlayerColor) : Boolean {
        for(piece in board.filterNotNull())
            if(piece !is King && piece.owner == player)
                return true

        return false
    }

    /**
     * @return The FEN representation of this GameState.
     * @see FEN
     */
    fun toFenString(): String {
        var fenData = ""
        for(r in 7 downTo 0) {
            var emptyCount = 0

            for(f in 0..7) {
                getPieceAt(Square(r, f))?.let { piece ->
                    if(emptyCount != 0) {
                        fenData += emptyCount.digitToChar()
                        emptyCount = 0
                    }
                    fenData += piece.fenLetter
                } ?: run {
                    emptyCount += 1
                }
            }

            if(emptyCount != 0)
                fenData += emptyCount.digitToChar()
            if(r != 0)
                fenData += '/'
        }

        fenData += (if(currentTurn==PlayerColor.WHITE) { " w " } else { " b " }) +
            "$castlingRights " +
            "${(enPassantTarget ?: "-")} " +
            "$halfmoveCounter " +
            "$fullmoveNumber"

        return fenData
    }

    /**
     * @return The internal reason of game over (or null, if there is none)
     * @see GameOverReason
     */
    fun isOver() : GameOverReason? {
        // This method only checks game over reasons within the single BoardState
        // That is checkmate, stalemate, insufficient material and 50 move rule
        val kingSquare = locateKing(currentTurn)
        if(getLegalMovesFor(kingSquare).isEmpty()) {
            return if(isInCheck(currentTurn))
                GameOverReason.CHECKMATE
            else
                GameOverReason.STALEMATE
        }

        if(halfmoveCounter >= 100)
            return GameOverReason.FIFTY_MOVE_RULE

        var whiteLight = 0
        var blackLight = 0
        for(piece in board.filterNotNull()) {
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
                Square(rank, file) to getPieceAt(Square(rank, file))
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
        if(_sa.contains("O-O")) {
            require(_sa == "O-O") { "Invalid move pattern." }
            require(if(currentTurn==PlayerColor.WHITE) {castlingRights.whiteKingSide} else {castlingRights.blackKingSide}) { "Castling rights invalid." }
            if(currentTurn==PlayerColor.WHITE) {move = Move(Square.fromString("e1"),Square.fromString("g1"))}
            else {move = Move(Square.fromString("e8"), Square.fromString("g8"))}
            verifyCheckmate(move)
            verifyCheck(move)
            require(isValidMove(move)) { "Move invalid." }
            return move
        }

        if(_sa.contains("O-O-O")) {
            require(_sa == "O-O") { "Invalid move pattern." }
            require(if(currentTurn==PlayerColor.WHITE) {castlingRights.whiteQueenSide} else {castlingRights.blackQueenSide}) { "Castling rights invalid." }
            if(currentTurn==PlayerColor.WHITE) {move = Move(Square.fromString("e1"),Square.fromString("c1"))}
            else {move = Move(Square.fromString("e8"), Square.fromString("c8"))}
            verifyCheckmate(move)
            verifyCheck(move)
            require(isValidMove(move)) { "Move invalid." }
            return move
        }

        // Promotion
        require(_sa.length >= 2) { "Missing target square information." }
        var promotionPiece : Move.Promotion? = null
        if(_sa[_sa.length-2] == '=') {
            promotionPiece = Move.Promotion.fromIdentifier(sa.last().lowercaseChar())
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
                //require(getPieceAt(destinationSquare) != null) { "Capture declared, but no piece to capture." }
                _sa = _sa.dropLast(1)
            }
        }
        require(requiresCapture || getPieceAt(destinationSquare) == null) { "No capture declared, but target square occupied." }

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
        } else {
            requiredPiece = Pawn(currentTurn)::class
        }

        // Finding the correct move
        var returnMove : Move? = null
        for(r in 0..7) {
            for(f in 0..7) {
                val fromSquare = Square(rank = r, file = f)
                val piece = getPieceAt(fromSquare) ?: continue
                if(piece.owner != currentTurn) continue
                if(piece::class != requiredPiece) continue
                if(rankDisambiguation != null) if(rankDisambiguation != r) continue
                if(fileDisambiguation != null) if(fileDisambiguation != f) continue
                val myMove = Move(fromSquare, destinationSquare, promotionPiece)
                if(!piece.getPieceVision(this, fromSquare).contains(myMove)) continue
                require(returnMove == null) { "Move definition ambiguous." }
                returnMove = myMove
            }
        }
        require(returnMove != null) { "No such move found to $destinationSquare" }
        if(requiresCheckmate) require(verifyCheckmate(returnMove)) { "Checkmate declared and not delivered." }
        if(requiresCheck) require(verifyCheck(returnMove)) { "Check declared and not delivered." }
        if(requiresCapture) require(getPieceAt(returnMove.from)!!.getCaptureVision(this, returnMove.from).contains(returnMove))
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
