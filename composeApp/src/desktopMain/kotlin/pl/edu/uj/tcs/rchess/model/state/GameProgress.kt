package pl.edu.uj.tcs.rchess.model.state

import pl.edu.uj.tcs.rchess.model.GameOverReason
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.PlayerColor
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
sealed class GameProgress {
    data class Running(
        val currentPlayerClock: ClockState.Running,
        val otherPlayerClock: ClockState.Paused,
    ) : GameProgress()

    open class Finished(
        val reason: GameOverReason,
        val result: GameResult
    ) : GameProgress()

    class FinishedWithClockInfo(
        reason: GameOverReason,
        result: GameResult,
        val whitePlayerClock: ClockState.Paused,
        val blackPlayerClock: ClockState.Paused,
    ): Finished(reason, result) {
        fun playerClock(color: PlayerColor) =
            when (color) {
                PlayerColor.WHITE -> whitePlayerClock
                PlayerColor.BLACK -> blackPlayerClock
            }
    }
}
