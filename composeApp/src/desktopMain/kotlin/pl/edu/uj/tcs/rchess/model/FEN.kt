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
            require(x.isDigit()) { "Halfmove number must contain only digits." }
        }
        require(segments[4].length <= 3) { "Halfmove number too large." }
        halfmoveCounter = segments[4].toInt()

        for(x in segments[5]) {
            require(x.isDigit()) { "Halfmove number must contain only digits." }
        }
        require(segments[5].length <= 8) { "Fullmove number too large." }
        fullmoveNumber = segments[5].toInt()

        require(segments[1][0] == 'b' || segments[1][0] == 'w') { "FEN must describe a proper color." }

        advancedValidityCheck()
    }

    private fun advancedValidityCheck() {
        require(halfmoveCounter >= 0) { "Halfmove counter must not be negative." }
        require(fullmoveNumber >= 0) { "Fullmove number must not be negative." }

        if(enPassantSquare != "-") {
            require(enPassantSquare.length == 2) { "En passant square is invalid." }
            require(enPassantSquare[0] in 'a'..'h') { "En passant square is invalid." }
            require(enPassantSquare[1].isDigit()) { "En passant square is invalid." }
            require(enPassantSquare[1].digitToInt() == 3 || enPassantSquare[1].digitToInt() == 6) { "En passant square is invalid." }
        }

        require(color == 'w' || color == 'b') { "FEN must describe the color to move." }

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
            require(i == 8) { "Each row must describe 8 squares." }
        }

        require(castling.isNotEmpty()) { "Castling descriptor must not be empty." }
        require(castling[0] == '-' || "KQkq".contains(castling[0])) { "Castling descriptor invalid." }

        //TODO: Uncomment after implementing fromFen in BoardState.
        //require(BoardState.fromFen(this).isLegal()) { throw IllegalArgumentException("Board describes invalid position.") }
    }
}