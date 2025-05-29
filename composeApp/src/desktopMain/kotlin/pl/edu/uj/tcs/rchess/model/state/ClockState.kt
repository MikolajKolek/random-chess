package pl.edu.uj.tcs.rchess.model.state

import pl.edu.uj.tcs.rchess.model.ClockSettings
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

sealed interface ClockState {
    val settings: ClockSettings

    /**
     * @return The remaining time on the clock, which does not include
     * the extra time before the first move. For that, see [remainingTotalTime]
     * or [remainingExtraTime].
     */
    fun remainingTimeOnClock(): Duration

    /**
     * @return The amount of remaining extra time that the player has
     * before their first move.
     *
     * If the extra time has ended, [remainingTimeOnClock] starts to decrease,
     * and this function returns [Duration.ZERO].
     */
    fun remainingExtraTime(): Duration

    /**
     * @return The total remaining time, including the time on the clock and
     * the extra time before the first move.
     */
    fun remainingTotalTime(): Duration = remainingTimeOnClock() + remainingExtraTime()


    fun toPausedAfterMove(): Paused

    fun toPausedWithoutMove(): Paused

    fun toRunning(): Running

    // TODO: In a client-server scenario, a time synchronization mechanism should be implemented,
    //  Instant might not be the best choice, as we cannot trust the the client's system clock to
    //  be the same as the server's.
    /**
     * Clock is counting down for this player.
     */
    sealed class Running : ClockState {
        protected abstract val endsAt: Instant
    }

    data class RunningBeforeFirstMove(override val settings: ClockSettings) : Running() {
        override val endsAt = Clock.System.now() + settings.startingTime + settings.extraTimeForFirstMove
        private val firstMoveTimeEndsAt = Clock.System.now() + settings.extraTimeForFirstMove

        override fun remainingTimeOnClock() =
            maxOf(Duration.ZERO, minOf(settings.startingTime, endsAt - Clock.System.now()))

        override fun remainingExtraTime() =
            maxOf(Duration.ZERO, firstMoveTimeEndsAt - Clock.System.now())

        override fun toPausedAfterMove() = PausedAfterFirstMove.fromRunningAfterMove(this)

        override fun toPausedWithoutMove() = PausedAfterFirstMove.fromRunningWithoutMove(this)

        override fun toRunning(): Running = this

        companion object {
            fun fromPaused(paused: Paused) = RunningBeforeFirstMove(paused.settings)
        }
    }

    @ConsistentCopyVisibility
    data class RunningAfterFirstMove private constructor(
        override val settings: ClockSettings,
        override val endsAt: Instant
    ) : Running() {
        override fun remainingTimeOnClock() = endsAt - Clock.System.now()

        override fun remainingExtraTime() = Duration.ZERO

        override fun toPausedAfterMove() = PausedAfterFirstMove.fromRunningAfterMove(this)

        override fun toPausedWithoutMove() = PausedAfterFirstMove.fromRunningWithoutMove(this)

        override fun toRunning() = this

        companion object {
            fun fromPaused(paused: Paused) = RunningAfterFirstMove(
                paused.settings,
                Clock.System.now() + paused.remainingTimeOnClock()
            )
        }
    }

    /**
     * Clock is not running for this player
     */
    sealed class Paused : ClockState

    data class PausedBeforeFirstMove(override val settings: ClockSettings) : Paused() {
        override fun remainingTimeOnClock() = settings.startingTime

        override fun remainingExtraTime() = settings.extraTimeForFirstMove

        override fun toPausedAfterMove() = this

        override fun toPausedWithoutMove() = this

        override fun toRunning() =
            RunningBeforeFirstMove.fromPaused(this)
    }

    @ConsistentCopyVisibility
    data class PausedAfterFirstMove private constructor(
        override val settings: ClockSettings,
        private val remainingTime: Duration
    ) : Paused() {
        override fun remainingTimeOnClock() = remainingTime

        override fun remainingExtraTime() = Duration.ZERO

        override fun toPausedAfterMove(): Paused = this

        override fun toPausedWithoutMove(): Paused = this

        override fun toRunning() = RunningAfterFirstMove.fromPaused(this)

        companion object {
            fun fromRunningAfterMove(running: Running) = PausedAfterFirstMove(
                running.settings,
                running.remainingTimeOnClock() + running.settings.moveIncrease
            )

            fun fromRunningWithoutMove(running: Running) = PausedAfterFirstMove(
                running.settings,
                running.remainingTimeOnClock()
            )
        }
    }
}
