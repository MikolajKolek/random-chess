package pl.edu.uj.tcs.rchess.model

/**
 * A class handling Forsyth-Edwards notation.
 */
class FEN(private val FENData: String = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1") {

    // TODO: Requires extensive testing

    val boardState : List<String>
    val color : Char
    val castling : String
    val enPassantSquare : String
    val halfmoveCounter : Int
    val fullmoveNumber : Int

    init {
        val segments: List<String> = FENData.split(" ")
        require(segments.size == 6) { throw IllegalArgumentException("FEN must contain six segments - it only contains " + segments.size) }

        boardState = segments[0].split("/")
        require(boardState.size == 8) { throw IllegalArgumentException("FEN must describe all eight rows.") }

        require(segments[1].length == 1) { throw IllegalArgumentException("FEN must include the color to move.") }
        color = segments[1][0]

        castling = segments[2]
        enPassantSquare = segments[3]

        for(x in segments[4]) {
            if(!x.isDigit()) { throw IllegalArgumentException("Halfmove number must contain only digits.") }
        }
        require(segments[4].length <= 3) { throw IllegalArgumentException("Halfmove number too large.") }
        halfmoveCounter = segments[4].toInt()

        for(x in segments[5]) {
            require(x.isDigit()) { throw IllegalArgumentException("Halfmove number must contain only digits.") }
        }
        require(segments[5].length <= 8) { throw IllegalArgumentException("Fullmove number too large.") }
        fullmoveNumber = segments[5].toInt()

        require(segments[1][0] == 'b' || segments[1][0] == 'w') { throw IllegalArgumentException("FEN must describe a proper color.") }

        advancedValidityCheck()
    }

    private fun advancedValidityCheck() {
        if(halfmoveCounter < 0) throw IllegalArgumentException("Halfmove counter must not be negative.")
        if(fullmoveNumber < 0) throw IllegalArgumentException("Fullmove number must not be negative.")

        if(enPassantSquare != "-") {
            if(enPassantSquare.length != 2) throw IllegalArgumentException("En passant square is invalid.")
            if(enPassantSquare[0] < 'a' || enPassantSquare[0] > 'h') throw IllegalArgumentException("En passant square is invalid.")
            if(!enPassantSquare[1].isDigit()) throw IllegalArgumentException("En passant square is invalid.")
            if(enPassantSquare[1].digitToInt() == 0 || enPassantSquare[1].digitToInt() == 9) throw IllegalArgumentException("En passant square is invalid.")
        }

        if(color != 'w' && color != 'b') throw IllegalArgumentException("FEN must describe the color to move.")

        for(row in boardState) {
            var i = 0
            for(x in row) {
                if(!"rnbqkp".contains(x, true)) {
                    if(!x.isDigit()) {
                        throw IllegalArgumentException("Invalid character in board description.")
                    } else {
                        i += x.digitToInt();
                    }
                } else {
                    i++;
                }
            }
            require(i == 8) { throw IllegalArgumentException("Each row must describe 8 squares.") }
        }

        require(castling.isNotEmpty()) { throw IllegalArgumentException("Castling descriptor must not be empty.") }
        if(castling[0] != '-' && !"KQkq".contains(castling[0])) {
            throw IllegalArgumentException("Castling descriptor invalid.")
        }

        //TODO: Uncomment after implementing fromFen in BoardState.
        //require(BoardState.fromFen(this).isLegal()) { throw IllegalArgumentException("Board describes invalid position.") }
    }
}