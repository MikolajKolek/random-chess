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

    /**
     * Returns a copy of this [CastlingRights] without king side castling rights for player [color].
     */
    fun withoutKing(color: PlayerColor): CastlingRights =
        when (color) {
            PlayerColor.WHITE -> copy(whiteKingSide = false)
            PlayerColor.BLACK -> copy(blackKingSide = false)
        }

    /**
     * Returns a copy of this [CastlingRights] without queen side castling rights for player [color].
     */
    fun withoutQueen(color: PlayerColor): CastlingRights =
        when (color) {
            PlayerColor.WHITE -> copy(whiteQueenSide = false)
            PlayerColor.BLACK -> copy(blackQueenSide = false)
        }

    /**
     * Returns a copy of this [CastlingRights] without all castling rights for player [color].
     */
    fun withoutBoth(color: PlayerColor) =
        withoutKing(color).withoutQueen(color)

    fun kingSide(color: PlayerColor) =
        when (color) {
            PlayerColor.WHITE -> whiteKingSide
            PlayerColor.BLACK -> blackKingSide
        }

    fun queenSide(color: PlayerColor) =
        when (color) {
            PlayerColor.WHITE -> whiteQueenSide
            PlayerColor.BLACK -> blackQueenSide
        }
}
