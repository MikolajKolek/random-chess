package pl.edu.uj.tcs.rchess.model

/**
 * Square represents one of the 64 playable squares of the chessboard.
 * @param row The row of the square. (0-7 - A-H)
 * @param col The column of the square.
 */
class Square(var row: Int, var col: Int, var piece: Piece? = null) {

    init {
        require(!(row < 0 || row > 7)) { "Row out of range." }
        require(!(col < 0 || col > 7)) { "Column out of range." }
        this.row = row
        this.col = col
    }
}
