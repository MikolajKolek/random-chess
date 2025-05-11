package pl.edu.uj.tcs.rchess.model

/**
 * A class handling Forsyth-Edwards notation.
 */
class FEN(fenData: String = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1") {
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


        require(BoardState.fromFen(this).isLegal()) {
            throw IllegalArgumentException("Board describes illegal position.")
        }
    }
}