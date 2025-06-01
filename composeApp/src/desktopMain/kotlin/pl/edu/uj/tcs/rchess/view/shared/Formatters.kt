import pl.edu.uj.tcs.rchess.api.entity.Service
import pl.edu.uj.tcs.rchess.model.*

fun PlayerColor.formatLowercase(): String
    = when (this) {
        PlayerColor.WHITE -> "white"
        PlayerColor.BLACK -> "black"
    }

fun PlayerColor.formatCapitalized(): String
    = when (this) {
        PlayerColor.WHITE -> "White"
        PlayerColor.BLACK -> "Black"
    }

fun GameWinReason.format(winner: PlayerColor): String
    = when (this) {
        GameWinReason.UNKNOWN -> "Win reason unknown"
        GameWinReason.TIMEOUT -> "${winner.opponent.formatCapitalized()} ran out of time"
        GameWinReason.CHECKMATE -> "Checkmate"
        GameWinReason.RESIGNATION -> "${winner.opponent.formatCapitalized()} resigned"
        GameWinReason.ABANDONMENT -> "${winner.opponent.formatCapitalized()} abandoned the game"
        GameWinReason.DEATH -> "\uD83D\uDC80"
    }

fun GameDrawReason.format(): String
    = when (this) {
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
