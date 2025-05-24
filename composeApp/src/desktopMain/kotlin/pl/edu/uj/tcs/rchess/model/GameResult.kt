package pl.edu.uj.tcs.rchess.model

import pl.edu.uj.tcs.rchess.db.enums.DbGameResult

enum class GameResult(val dbResult: DbGameResult, val pgnString: String) {
    WHITE_WON(DbGameResult.white_won, "1-0"),
    BLACK_WON(DbGameResult.black_won, "0-1"),
    DRAW(DbGameResult.draw, "1/2-1/2");

    companion object {
        fun fromDbResult(result: DbGameResult): GameResult = entries.find { it.dbResult == result }
            ?: throw IllegalArgumentException("Invalid db game result : $result")

        fun fromPgnString(string: String): GameResult = entries.find { it.pgnString == string }
            ?: throw IllegalArgumentException("Invalid pgn result string: $string")

        fun winFromPlayerColor(color: PlayerColor) = when(color) {
            PlayerColor.WHITE -> WHITE_WON
            PlayerColor.BLACK -> BLACK_WON
        }
    }
}
