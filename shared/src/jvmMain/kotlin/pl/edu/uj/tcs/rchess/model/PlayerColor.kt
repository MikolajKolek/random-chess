package pl.edu.uj.tcs.rchess.model

enum class PlayerColor(
    /**
     * The rank at which this player can promote pawns
     */
    val promotionRank: Int,

    /**
     * The rank to which this player's pawns move in en passant moves
     */
    val enPassantTargetRank: Int,

    /**
     * The rank from which the player's pawns can start a double-square move
     */
    val pawnDoubleMoveRank: Int,

    /**
     * A Unicode symbol representing this player color
     */
    val unicodeSymbol: String,
) {
    WHITE(promotionRank = 7, enPassantTargetRank = 5, pawnDoubleMoveRank = 1, unicodeSymbol = "♙"),
    BLACK(promotionRank = 0, enPassantTargetRank = 2, pawnDoubleMoveRank = 6, unicodeSymbol = "♟");

    val opponent: PlayerColor
        get() = when (this) {
            WHITE -> BLACK
            BLACK -> WHITE
        }

    companion object {
        fun fromPgnWinString(string: String) = when(string) {
            "1-0" -> WHITE
            "0-1" -> BLACK
            else -> throw IllegalArgumentException("Invalid pgn result string")
        }
    }
}
