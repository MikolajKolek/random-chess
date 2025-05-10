package pl.edu.uj.tcs.rchess.model

enum class PlayerColor {
    WHITE,
    BLACK;

    fun getOpponent() : PlayerColor {
        return if(this == WHITE) BLACK else WHITE
    }
}
