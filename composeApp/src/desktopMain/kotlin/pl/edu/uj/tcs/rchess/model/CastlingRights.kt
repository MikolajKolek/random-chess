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

        fun fromString(value: String): CastlingRights {
            return CastlingRights(
                whiteKingSide = value.contains("K"),
                whiteQueenSide = value.contains("Q"),
                blackKingSide = value.contains("k"),
                blackQueenSide = value.contains("q"),
            )
        }
    }

    override fun toString(): String {
        if (!whiteKingSide && !whiteQueenSide && !blackKingSide && !blackQueenSide)
            return "-"

        return buildString {
            if (whiteKingSide) append('K')
            if (whiteQueenSide) append('Q')
            if (blackKingSide) append('k')
            if (blackQueenSide) append('q')
        }
    }

    fun withoutKing(color: PlayerColor): CastlingRights =
        when (color) {
            PlayerColor.WHITE -> copy(whiteKingSide = false)
            PlayerColor.BLACK -> copy(blackKingSide = false)
        }

    fun withoutQueen(color: PlayerColor): CastlingRights =
        when (color) {
            PlayerColor.WHITE -> copy(whiteQueenSide = false)
            PlayerColor.BLACK -> copy(blackQueenSide = false)
        }

    fun withoutBoth(color: PlayerColor) =
        withoutKing(color).withoutQueen(color)
}
