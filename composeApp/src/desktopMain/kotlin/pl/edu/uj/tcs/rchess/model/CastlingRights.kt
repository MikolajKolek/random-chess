package pl.edu.uj.tcs.rchess.model

data class CastlingRights(
    val whiteKingSide: Boolean,
    val whiteQueenSide: Boolean,
    val blackKingSide: Boolean,
    val blackQueenSide: Boolean,
) {
    companion object {
        fun full() = CastlingRights(
            whiteKingSide = true,
            whiteQueenSide = true,
            blackKingSide = true,
            blackQueenSide = true
        )
        fun fromArray(values: Array<Boolean>): CastlingRights {
            require(values.size == 4) {"Need to initialize all four values."}
            return CastlingRights(
                whiteKingSide = values[0],
                whiteQueenSide = values[1],
                blackKingSide = values[2],
                blackQueenSide = values[3]
            )
        }
    }
}
