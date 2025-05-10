package pl.edu.uj.tcs.rchess.model.state

import pl.edu.uj.tcs.rchess.model.GameOverReason
import pl.edu.uj.tcs.rchess.model.PlayerColor
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
sealed class GameProgress {
    data class Running(
        val currentPlayerTimeout: ClockState.Running,
        val otherPlayerRemainingTime: ClockState.Paused,
    ) : GameProgress()

    data class Finished(
        val whitePlayerClock: ClockState.Paused,
        val blackPlayerClock: ClockState.Paused,
        val reason: GameOverReason,
        // TODO: Add winner
    ) : GameProgress() {
        fun playerClock(color: PlayerColor) =
            when (color) {
                PlayerColor.WHITE -> whitePlayerClock
                PlayerColor.BLACK -> blackPlayerClock
            }
    }
}
