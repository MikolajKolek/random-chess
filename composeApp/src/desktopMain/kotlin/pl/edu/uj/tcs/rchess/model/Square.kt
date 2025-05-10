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

    companion object {
        fun fromString(s : String) : Square {
            require(s.length == 2) {"Square notation must have two characters."}
            require(s[0] in 'a'..'h') {"Rank value invalid"}
            require(s[1] in '1'..'8') {"File value invalid"}

            return Square((s[0].digitToInt() - 'a'.digitToInt()), (s[1].digitToInt()-1))
        }
    }

    val isDark = (rank + file) % 2 == 0

    override fun toString(): String {
        return (rank + 'a'.digitToInt()).toChar() + file.toString()
    }
}
