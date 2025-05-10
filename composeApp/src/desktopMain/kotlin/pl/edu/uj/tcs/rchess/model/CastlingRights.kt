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
            var ret = CastlingRights.full()
            if(!value.contains("K")) ret = ret.copy(whiteKingSide = false)
            if(!value.contains("Q")) ret = ret.copy(whiteQueenSide = false)
            if(!value.contains("k")) ret = ret.copy(blackKingSide = false)
            if(!value.contains("q")) ret = ret.copy(blackQueenSide = false)
            return ret
        }
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
