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
}
