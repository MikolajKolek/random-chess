package pl.edu.uj.tcs.rchess.model

sealed interface GameResult {
    fun toPgnString(): String

    val winner: PlayerColor?

    companion object {}
}

data class Win(val winReason: GameWinReason, override val winner: PlayerColor) : GameResult {
    override fun toPgnString(): String = when (winner) {
        PlayerColor.WHITE -> "1-0"
        PlayerColor.BLACK -> "0-1"
    }
}

data class Draw(val drawReason: GameDrawReason) : GameResult {
    override val winner: Nothing? = null

    override fun toPgnString(): String = "1/2-1/2"
}
