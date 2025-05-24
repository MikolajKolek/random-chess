package pl.edu.uj.tcs.rchess.model

import pl.edu.uj.tcs.rchess.db.udt.records.GameResultTypeRecord

sealed class GameResult() {
    abstract fun toPgnString(): String

    abstract fun toDbResult(): GameResultTypeRecord

    companion object {
        //TODO: maybe move this out of here? somewhere into server? idk it's 1:21 am
        fun fromDbResult(result: GameResultTypeRecord): GameResult {
            return when(result.gameEndType) {
                "1-0" -> Win(GameWinReason.fromDbString(result.gameEndReason), PlayerColor.WHITE)
                "0-1" -> Win(GameWinReason.fromDbString(result.gameEndReason), PlayerColor.BLACK)
                "1/2-1/2" -> Draw(GameDrawReason.fromDbString(result.gameEndReason))
                else -> throw IllegalArgumentException("Invalid db game_result")
            }
        }

        fun fromPgnString(string: String): GameResult = when(string) {
            "1-0" -> Win(GameWinReason.UNKNOWN, PlayerColor.WHITE)
            "0-1" -> Win(GameWinReason.UNKNOWN, PlayerColor.BLACK)
            "1/2-1/2" -> Draw(GameDrawReason.UNKNOWN)
            else -> throw IllegalArgumentException("Invalid pgn result string")
        }

        fun winFromPlayerColor(color: PlayerColor, winReason: GameWinReason) = when(color) {
            PlayerColor.WHITE -> Win(winReason, PlayerColor.WHITE)
            PlayerColor.BLACK -> Win(winReason, PlayerColor.BLACK)
        }
    }
}

class Win(val winReason: GameWinReason, val winner: PlayerColor) : GameResult() {
    override fun toPgnString(): String = when(winner) {
        PlayerColor.WHITE -> "1-0"
        PlayerColor.BLACK -> "0-1"
    }

    override fun toDbResult(): GameResultTypeRecord =
        GameResultTypeRecord(gameEndType = toPgnString(), gameEndReason = winReason.toDbWinReason())
}

class Draw(val drawReason: GameDrawReason) : GameResult() {
    override fun toPgnString(): String = "1/2-1/2"

    override fun toDbResult(): GameResultTypeRecord =
        GameResultTypeRecord(gameEndType = toPgnString(), gameEndReason = drawReason.toDbWinReason())
}
