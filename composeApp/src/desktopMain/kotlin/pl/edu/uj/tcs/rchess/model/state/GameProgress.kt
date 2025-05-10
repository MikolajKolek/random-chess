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

    data class Finished(
        val whitePlayerClock: ClockState.Paused,
        val blackPlayerClock: ClockState.Paused,
        val reason: GameOverReason,
        val result: GameResult
    ) : GameProgress() {
        fun playerClock(color: PlayerColor) =
            when (color) {
                PlayerColor.WHITE -> whitePlayerClock
                PlayerColor.BLACK -> blackPlayerClock
            }
    }
}
