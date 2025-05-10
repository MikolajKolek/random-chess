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

        fun fromString(value : String) : CastlingRights {
            return CastlingRights(
                whiteKingSide = value.contains("K"),
                whiteQueenSide = value.contains("Q"),
                blackKingSide = value.contains("k"),
                blackQueenSide = value.contains("q"),
            )
        }
    }

    override fun toString(): String {
        if(!whiteKingSide && !whiteQueenSide && !blackKingSide && !blackQueenSide)
            return "-"

        var res = ""

        if(whiteKingSide) res += "K"
        if(whiteQueenSide) res += "Q"
        if(blackKingSide) res += "k"
        if(blackQueenSide) res += "q"

        return res
    }
}
