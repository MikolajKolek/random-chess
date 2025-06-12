package pl.edu.uj.tcs.rchess.external.lichess

import pl.edu.uj.tcs.rchess.api.entity.Service
import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.external.lichess.entity.EmptyLichessPlayer
import pl.edu.uj.tcs.rchess.external.lichess.entity.LichessAccount
import pl.edu.uj.tcs.rchess.external.lichess.entity.LichessBot
import pl.edu.uj.tcs.rchess.external.lichess.entity.LichessClock
import pl.edu.uj.tcs.rchess.external.lichess.entity.LichessPlayer
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.Draw
import pl.edu.uj.tcs.rchess.model.GameDrawReason
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.GameWinReason
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Win
import kotlin.time.Duration.Companion.seconds

object LichessSerialization {
    internal fun GameResult.Companion.fromLichessStatus(lichessStatus: String, winner: String?): GameResult {
        val winColor = winner?.let {
            when (it) {
                "black" -> PlayerColor.BLACK
                "white" -> PlayerColor.WHITE
                else -> null
            }
        }

        return when (lichessStatus) {
            "mate" -> Win(GameWinReason.CHECKMATE, winColor!!)
            "draw" -> Draw(GameDrawReason.UNKNOWN)
            "stalemate" -> Draw(GameDrawReason.STALEMATE)
            "outoftime", "timeout" -> winColor?.let { Win(GameWinReason.TIMEOUT, it) }
                ?: Draw(GameDrawReason.TIMEOUT_VS_INSUFFICIENT_MATERIAL)
            "resign" -> Win(GameWinReason.RESIGNATION, winColor!!)
            else -> winColor?.let { Win(GameWinReason.UNKNOWN, it) }
                ?: Draw(GameDrawReason.UNKNOWN)
        }
    }

    internal fun ServiceAccount.Companion.fromLichessPlayer(lichessPlayer: LichessPlayer): ServiceAccount? {
        return when (lichessPlayer) {
            is EmptyLichessPlayer -> null
            is LichessAccount -> ServiceAccount(
                service = Service.LICHESS,
                userIdInService = lichessPlayer.user.id,
                displayName = lichessPlayer.user.name,
                isBot = lichessPlayer.user.title == "BOT",
                isCurrentUser = false,
            )

            is LichessBot -> ServiceAccount(
                service = Service.LICHESS,
                userIdInService = lichessPlayer.aiLevel.toString(),
                displayName = "Lichess bot (Level ${lichessPlayer.aiLevel})",
                isBot = true,
                isCurrentUser = false,
            )
        }
    }

    internal fun ClockSettings.Companion.fromLichessClock(clock: LichessClock) = ClockSettings(
        startingTime = clock.initial.seconds,
        moveIncrease = clock.increment.seconds,
        extraTimeForFirstMove = 0.seconds
    )
}