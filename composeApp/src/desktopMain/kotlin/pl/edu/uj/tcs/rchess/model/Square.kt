package pl.edu.uj.tcs.rchess.model

/**
 * @param rank The row of the square. (0-7 - A-H)
 * @param file The column of the square.
 */
data class Square(
    val rank: Int,
    val file: Int,
) {
    init {
        require(!(rank < 0 || rank > 7)) { "Row out of range." }
        require(!(file < 0 || file > 7)) { "Column out of range." }
    }

    val isDark = (rank + file) % 2 == 0
}
