import pl.edu.uj.tcs.rchess.model.GameDrawReason
import pl.edu.uj.tcs.rchess.model.GameWinReason
import pl.edu.uj.tcs.rchess.model.PlayerColor

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
