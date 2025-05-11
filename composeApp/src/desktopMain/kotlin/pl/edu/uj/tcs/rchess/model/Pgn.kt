package pl.edu.uj.tcs.rchess.model

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.time.LocalDateTime

private val pgnGameRegex = Regex("((\\[.*]\\n)*)\\n(.*(1-0|0-1|1/2-1/2|\\*))")

class Pgn private constructor(pgnGameRegexMatch: MatchResult) {
    val moves: List<Move>
    val result: GameResult
    val metadata: JsonObject?
    val blackPlayerName: String
    val whitePlayerName: String

    init {
        pgnTagStringToTags(pgnGameRegexMatch.groupValues[1]).let { tags ->
            moves = pgnMovetextToMoves(pgnGameRegexMatch.groupValues[3])
            result = GameResult.fromPgnString(tags["Result"]!!)
            metadata = JsonObject(
                tags.toMap().filter { it.key != "White" && it.key != "Black" && it.key != "Result" }
                    .mapValues { JsonPrimitive(it.value) }
            )
            blackPlayerName = tags["Black"]!!
            whitePlayerName = tags["White"]!!
        }
    }

    constructor(singlePgnGame: String) : this(
        pgnGameRegex.find(singlePgnGame)
            ?: throw IllegalArgumentException("Invalid PGN string.")
    )

    private fun pgnTagStringToTags(tags: String): Map<String, String> {
        val regex = Regex("\\[\\s*([a-zA-Z0-9_]+)\\s*\"([^\"]+)\"]\n")

        val map = mutableMapOf<String, String>()
        for(match in regex.findAll(tags))
            map[match.groupValues[1]] = match.groupValues[2]

        return map
    }

    private fun pgnMovetextToMoves(movetext: String): List<Move> {
        val moves = movetext
            .replace(Regex("\\{[^}]*}"), " ")               // komentarze multi-line
            .replace(Regex(";.+$"), " ")                    // komentarze single-line
            .replace(Regex("\\$\\d+"), " ")                 // NAG
            .replace(Regex("\\(.*\\)"), " ")                // RAV
            .replace(Regex("\\d+\\.(\\.\\.)?\\s*"), " ")    // numery tur
            .trim()
            .replace(Regex("\\s*(1-0|0-1|1/2-1/2|\\*)\\s*$"), "")
            .split(Regex("\\s+"))

        var boardState = BoardState.initial()
        val result = mutableListOf<Move>()

        for(move in moves) {
            result.add(boardState.standardAlgebraicToMove(move))
            boardState = boardState.applyMove(result.last())
        }

        return result
    }

    private fun pgnDateToLocalDateTime(date: String): LocalDateTime {
        val pgnDateRegex = Regex("([\\d?]{4})\\.([\\d?]{2})\\.([\\d?]{2})")
        var (year, month, day) = pgnDateRegex.find(date)!!.destructured

        if(year.contains('?'))
            year = LocalDateTime.now().year.toString()
        if(month.contains('?')) {
            month = if(year == LocalDateTime.now().year.toString())
                LocalDateTime.now().monthValue.toString()
            else
                "01"
        }
        if(day.contains('?')) {
            day = if(year == LocalDateTime.now().year.toString() && month == LocalDateTime.now().monthValue.toString())
                LocalDateTime.now().dayOfMonth.toString()
            else
                "01"
        }

        return LocalDateTime.of(year.toInt(), month.toInt(), day.toInt(), 0, 0)
    }

    companion object {
        /**
         * @return A list of [Pgn] objects parsed from the given [pgnDatabase] string.
         * @throws IllegalArgumentException if the PGN database is invalid.
         */
        fun fromPgnDatabase(pgnDatabase: String): List<Pgn> =
            pgnGameRegex.findAll(pgnDatabase).map { match -> Pgn(match) }.toList()
    }
}