package pl.edu.uj.tcs.rchess.model

import pl.edu.uj.tcs.rchess.generated.db.udt.records.GameResultTypeRecord

sealed interface GameResult {
    fun toPgnString(): String

    fun toDbResult(): GameResultTypeRecord

    val winner: PlayerColor?

    companion object {
        //TODO: maybe move this out of here? somewhere into server? idk it's 1:21 am
        fun fromDbResult(result: GameResultTypeRecord): GameResult {
            return when (result.gameEndType) {
                "1-0" -> Win(GameWinReason.fromDbString(result.gameEndReason), PlayerColor.WHITE)
                "0-1" -> Win(GameWinReason.fromDbString(result.gameEndReason), PlayerColor.BLACK)
                "1/2-1/2" -> Draw(GameDrawReason.fromDbString(result.gameEndReason))
                else -> throw IllegalArgumentException("Invalid db game_result")
            }
        }
    }
}

data class Win(val winReason: GameWinReason, override val winner: PlayerColor) : GameResult {
    override fun toPgnString(): String = when (winner) {
        PlayerColor.WHITE -> "1-0"
        PlayerColor.BLACK -> "0-1"
    }

    override fun toDbResult(): GameResultTypeRecord =
        GameResultTypeRecord(gameEndType = toPgnString(), gameEndReason = winReason.toDbWinReason())
}

data class Draw(val drawReason: GameDrawReason) : GameResult {
    override val winner: Nothing? = null

    override fun toPgnString(): String = "1/2-1/2"

    override fun toDbResult(): GameResultTypeRecord =
        GameResultTypeRecord(gameEndType = toPgnString(), gameEndReason = drawReason.toDbWinReason())
}
