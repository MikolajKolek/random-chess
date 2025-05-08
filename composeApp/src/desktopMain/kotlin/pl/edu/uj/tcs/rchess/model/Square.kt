package pl.edu.uj.tcs.rchess.model

/**
 * @param row The row of the square. (0-7 - A-H)
 * @param col The column of the square.
 */
data class Square(
    val row: Int,
    val col: Int,
) {
    init {
        require(!(row < 0 || row > 7)) { "Row out of range." }
        require(!(col < 0 || col > 7)) { "Column out of range." }
    }
}
