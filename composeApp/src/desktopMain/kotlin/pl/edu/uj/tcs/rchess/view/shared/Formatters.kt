import pl.edu.uj.tcs.rchess.api.entity.Service
import pl.edu.uj.tcs.rchess.model.Draw
import pl.edu.uj.tcs.rchess.model.GameDrawReason
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.GameWinReason
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Win
import kotlin.time.Duration

fun PlayerColor.formatLowercase(): String = when (this) {
    PlayerColor.WHITE -> "white"
    PlayerColor.BLACK -> "black"
}

fun PlayerColor.formatCapitalized(): String = when (this) {
    PlayerColor.WHITE -> "White"
    PlayerColor.BLACK -> "Black"
}

fun GameWinReason.format(winner: PlayerColor): String = when (this) {
    GameWinReason.UNKNOWN -> "Win reason unknown"
    GameWinReason.TIMEOUT -> "${winner.opponent.formatCapitalized()} ran out of time"
    GameWinReason.CHECKMATE -> "Checkmate"
    GameWinReason.RESIGNATION -> "${winner.opponent.formatCapitalized()} resigned"
    GameWinReason.ABANDONMENT -> "${winner.opponent.formatCapitalized()} abandoned the game"
    GameWinReason.DEATH -> "\uD83D\uDC80"
}

fun GameDrawReason.format(): String = when (this) {
    GameDrawReason.UNKNOWN -> "Draw reason unknown"
    GameDrawReason.TIMEOUT_VS_INSUFFICIENT_MATERIAL -> "Timeout vs insufficient material"
    GameDrawReason.INSUFFICIENT_MATERIAL -> "Insufficient material"
    GameDrawReason.THREEFOLD_REPETITION -> "Threefold repetition"
    GameDrawReason.FIFTY_MOVE_RULE -> "Fifty move rule"
    GameDrawReason.STALEMATE -> "Stalemate"
}

fun GameResult.formatResult() = when (this) {
    is Win -> "${winner.formatCapitalized()} won"
    is Draw -> "Draw"
}

fun GameResult.formatReason() = when (this) {
    is Win -> winReason.format(winner)
    is Draw -> drawReason.format()
}

fun Service?.format() = when (this) {
    null -> "Imported"
    Service.RANDOM_CHESS -> "Random Chess"
    Service.CHESS_COM -> "Chess.com"
    Service.LICHESS -> "Lichess"
    Service.UNKNOWN -> "Unknown"
}

fun Duration.formatHuman(alwaysShowMinutes: Boolean = true) =
    toComponents { hours, minutes, seconds, nanoseconds ->
        val centisecond = nanoseconds / 10_000_000

        when {
            hours > 0 -> {
                "%d:%02d:%02d.%02d".format(hours, minutes, seconds, centisecond)
            }
            minutes > 0 || alwaysShowMinutes -> {
                "%d:%02d.%02d".format(minutes, seconds, centisecond)
            }
            else -> {
                "%02d.%02d".format(seconds, centisecond)
            }
        }
    }

/**
 * Format this duration assuming it represents clock settings
 */
fun Duration.formatHumanSetting(): String {
    if (isInfinite()) return "∞"

    return toComponents { hours, minutes, seconds, nanoseconds ->
        buildList {
            fun addPart(number: Long, single: String, plural: String, evenIfZero: Boolean = false) {
                if (number == 0L && !evenIfZero) return
                val text = if (number == 1L) single else plural
                add("$number $text")
            }

            addPart(hours, "hour", "hours")
            addPart(minutes.toLong(), "minute", "minutes", evenIfZero = hours == 0L && seconds == 0)
            addPart(seconds.toLong(), "second", "seconds")
        }
    }.joinToString(", ")
}

fun Int.formatWithSign() =
    when {
        this < 0 -> this.toString()
        else -> "+$this"
    }