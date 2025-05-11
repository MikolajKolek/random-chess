package pl.edu.uj.tcs.rchess.model

import pl.edu.uj.tcs.rchess.model.board.MutableBoard
import pl.edu.uj.tcs.rchess.model.pieces.Piece

/**
 * A class handling Forsyth-Edwards notation.
 */
class Fen private constructor(fenData: String) {
    val boardState : List<String>
    val color : Char
    val castling : String
    val enPassantSquare : String
    val halfmoveCounter : Int
    val fullmoveNumber : Int

    init {
        val segments: List<String> = fenData.split(" ")
        require(segments.size == 6) { "FEN must contain six segments - it contains ${segments.size}." }


        boardState = segments[0].split("/")
        require(boardState.size == 8) { "FEN must describe all eight rows." }

        for(row in boardState) {
            var i = 0

            for(x in row) {
                if(!"rnbqkp".contains(x, true)) {
                    if(!x.isDigit())
                        throw IllegalArgumentException("Invalid character in board description: $boardState")
                    else
                        i += x.digitToInt()
                }
                else
                    i++
            }

            require(i == 8) { "Each row must describe 8 squares." }
        }

        color = segments[1].let {
            require(it.length == 1) { "FEN must include the color to move." }
            require(it[0] == 'b' || it[0] == 'w') { "FEN must describe a proper color." }
            it[0]
        }


        castling = segments[2]
        require(castling.isNotEmpty()) { "Castling descriptor must not be empty." }
        require(castling[0] == '-' ||
                (castling.count { "KQkq".contains(it) } == castling.length &&
                        castling.toSet().size == castling.length)
        ) { "Castling descriptor invalid." }


        enPassantSquare = segments[3]
        if(enPassantSquare != "-") {
            val square = Square.fromStringOrNull(enPassantSquare)
                ?: throw IllegalArgumentException("En passant square is invalid.")

            // square.rank counts from 0 upwards, so rank == 2 is rank 3, and rank == 5 is rank 6
            require(square.rank == 2 || square.rank == 5) { "En passant square is invalid." }
        }


        halfmoveCounter = segments[4].let { counter ->
            counter.forEach { require(it.isDigit()) { "Halfmove counter must contain only digits."} }
            require(counter.length <= 3) { "Halfmove counter too large." }
            counter.toInt()
        }
        require(halfmoveCounter >= 0) { "Halfmove counter must not be negative." }


        fullmoveNumber = segments[5].let { number ->
            number.forEach { require(it.isDigit()) { "Fullmove number must contain only digits."} }
            require(number.length <= 3) { "Fullmove number too large." }
            number.toInt()
        }
        require(fullmoveNumber >= 0) { "Fullmove number must not be negative." }
    }

    companion object {
        /**
         * Standard initial position for a board in FEN notation
         */
        const val INITIAL: String = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

        /**
         * Extension method on [BoardState] to convert it to a FEN string.
         *
         * @return The FEN representation of this board state.
         */
        fun BoardState.toFenString(): String = buildString {
            for (r in 7 downTo 0) {
                var emptyCount = 0

                for (f in 0..7) {
                    board[Square(r, f)]?.let { piece ->
                        if (emptyCount != 0) {
                            this.append(emptyCount.digitToChar())
                            emptyCount = 0
                        }
                        this.append(piece.fenLetter)
                    } ?: run {
                        emptyCount += 1
                    }
                }

                if (emptyCount != 0)
                    this.append(emptyCount.digitToChar())
                if (r != 0)
                    this.append('/')
            }

            this.append(" ")
            this.append(
                when (currentTurn) {
                    PlayerColor.WHITE -> "w"
                    PlayerColor.BLACK -> "b"
                }
            )
            this.append(" ")
            this.append(castlingRights.toString())
            this.append(" ")
            this.append(enPassantTarget?.toString() ?: "-")
            this.append(" ")
            this.append(halfmoveCounter)
            this.append(" ")
            this.append(fullmoveNumber)
        }

        /**
         * Extension method on [BoardState] to create a new instance from a FEN string.
         */
        fun BoardState.Companion.fromFen(fenString: String): BoardState {
            val fen = Fen(fenString)

            val newBoard = MutableBoard.empty()
            for(row in 7 downTo 0) {
                val fenRow = fen.boardState[7 - row]
                var column = 0

                for(v in fenRow) {
                    if(v.isDigit())
                        column += v.digitToInt()
                    else {
                        newBoard[Square(rank = row, file = column)] = Piece.fromFenLetter(v)
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
            ).also {
                require(it.isLegal()) { "FEN describes illegal board position." }
            }
        }
    }
}
