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

            return Square((s[0].code - 'a'.code), (s[1].digitToInt() - 1))
        }

        fun fromStringOrNull(s : String) : Square? {
            if(s.length != 2 || s[0] !in 'a'..'h' || s[1] !in '1'..'8')
                return null

            return Square((s[0].code - 'a'.code), (s[1].digitToInt() - 1))
        }

        fun squareOrNull(rank : Int, file : Int) : Square? {
            if(rank in 0..7 && file in 0..7)
                return Square(rank, file)

            return null
        }
    }

    val isDark = (rank + file) % 2 == 0

    fun positionInBoard() = (8 * rank) + file

    override fun toString(): String {
        return (rank + 'a'.code).toChar() + (file + 1).toString()
    }

    data class Vector(
        val rank: Int,
        val file: Int,
    ) {
        operator fun unaryMinus() = Vector(-rank, -file)

        operator fun times(n : Int) = Vector(rank * n, file * n)
    }

    operator fun plus(vector : Vector) : Square? =
        squareOrNull(rank + vector.rank, file + vector.file)

    operator fun minus(vector : Vector) : Square? =
        squareOrNull(rank - vector.rank, file - vector.file)
}
