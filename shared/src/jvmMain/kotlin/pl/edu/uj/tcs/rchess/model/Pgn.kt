package pl.edu.uj.tcs.rchess.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import pl.edu.uj.tcs.rchess.model.Fen.Companion.fromFen
import pl.edu.uj.tcs.rchess.model.state.BoardState

class Pgn private constructor(pgnGameRegexMatch: MatchResult) {
    val moves: List<Move>
    val startingPosition: BoardState
    val result: GameResult
    val metadata: JsonObject?
    val blackPlayerName: String
    val whitePlayerName: String

    init {
        pgnTagStringToTags(pgnGameRegexMatch.groupValues[1]).let { tags ->
            startingPosition = tags["FEN"]?.let { BoardState.fromFen(it) } ?: BoardState.initial
            val (moveList, state) = processPgnMovetext(pgnGameRegexMatch.groupValues[3])
            moves = moveList

            val res = tags["Result"] ?: throw IllegalArgumentException("The PGN does not contain the Result tag")
            require(res != "*") { "RandomChess does not allow for the importing of ongoing PGN games" }
            require(res == "1-0" || res == "0-1" || res == "1/2-1/2") { "Invalid pgn result string" }

            if(res == "1/2-1/2")
                result = Draw(
                    when(tags["Termination"]?.lowercase()) {
                        "time forfeit" -> GameDrawReason.TIMEOUT_VS_INSUFFICIENT_MATERIAL
                        else -> GameDrawReason.UNKNOWN
                    }
                )
            else {
                var reason: GameWinReason = when(tags["Termination"]?.lowercase()) {
                    "abandoned" -> GameWinReason.ABANDONMENT
                    "death" -> GameWinReason.DEATH
                    "time forfeit" -> GameWinReason.TIMEOUT
                    else -> GameWinReason.UNKNOWN
                }

                val impliedGameOverReason = state.impliedGameOverReason()
                if(reason == GameWinReason.UNKNOWN && impliedGameOverReason is Win)
                    reason = impliedGameOverReason.winReason

                result = Win(reason, PlayerColor.fromPgnWinString(res))
            }

            metadata = JsonObject(
                tags.toMap().filter { it.key != "White" && it.key != "Black" && it.key != "Result" }
                    .mapValues { JsonPrimitive(it.value ) }
            )
            blackPlayerName = tags["Black"]
                ?: throw IllegalArgumentException("The PGN does not contain the Black tag")
            whitePlayerName = tags["White"]
                ?: throw IllegalArgumentException("The PGN does not contain the White tag")
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

    private fun processPgnMovetext(movetext: String): Pair<List<Move>, BoardState> {
        val withoutRAV = StringBuilder()
        var ravDepth = 0
        for(c in movetext) {
            if(c == '(')
                ravDepth++
            else if(c == ')') {
                if(ravDepth == 0)
                    throw IllegalArgumentException("Invalid RAV in PGN.")

                ravDepth--
            }
            else if(ravDepth == 0)
                withoutRAV.append(c)
        }

        val moves = withoutRAV
            .replace(Regex("\\{[^}]*}"), " ")               // komentarze multi-line
            .replace(Regex(";.+$"), " ")                    // komentarze single-line
            .replace(Regex("\\$\\d+"), " ")                 // NAG
            .replace(Regex("\\d+\\.(\\.\\.)?\\s*"), " ")    // numery tur
            .trim()
            .replace(Regex("\\s*(1-0|0-1|1/2-1/2|\\*)\\s*$"), "")
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .map { it.replace(Regex("(!|\\?|!!|!\\?|\\?!|\\?\\?)"), "") }

        var boardState = startingPosition
        val result = mutableListOf<Move>()

        for(move in moves) {
            result.add(boardState.standardAlgebraicToMove(move))
            boardState = boardState.applyMove(result.last())
        }

        return Pair(result, boardState)
    }

    companion object {
        private val pgnGameRegex = Regex("((\\[.*]\\n)*)\\n(.*(1-0|0-1|1/2-1/2|\\*))")

        /**
         * @return A list of [Pgn] objects parsed from the given [pgnDatabase] string.
         * @throws IllegalArgumentException if the PGN database is invalid.
         */
        suspend fun fromPgnDatabase(pgnDatabase: String): List<Pgn> = coroutineScope {
            pgnGameRegex.findAll(pgnDatabase).map {
                async(Dispatchers.Default) {
                    Pgn(it)
                }
            }.toList().awaitAll()
        }
    }
}
