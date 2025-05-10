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
    }

    override fun toString(): String {
        if(!whiteKingSide && !blackKingSide && whiteQueenSide && !blackQueenSide) {
            return "-"
        }
        var res = ""
        if(whiteKingSide) res += "K"
        if(whiteQueenSide) res += "Q"
        if(blackKingSide) res += "k"
        if(blackQueenSide) res += "q"
        return res
    }
}
