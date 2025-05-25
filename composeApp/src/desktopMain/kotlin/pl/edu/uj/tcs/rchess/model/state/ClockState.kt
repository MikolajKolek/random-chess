package pl.edu.uj.tcs.rchess.model.state

import pl.edu.uj.tcs.rchess.model.ClockSettings
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

sealed interface ClockState {
    val settings: ClockSettings

    /**
     * Returns the remaining time on the clock, which does not include the extra time before the first move. For that, see [remainingTotalTime] or [RunningBeforeFirstMove.remainingExtraTime]
     */
    fun remainingTimeOnClock(): Duration

    /**
     * Returns the remaining time, including the time on the clock and the extra time before the first move.
     */
    fun remainingTotalTime(): Duration

    fun toPausedAfterMove(): Paused

    fun toPausedWithoutMove(): Paused

    fun toRunning(): Running

    // TODO: In a client-server scenario, a time synchronization mechanism should be implemented,
    //  Instant might not be the best choice, as we cannot trust the the client's system clock to
    //  be the same as the server's.
    /**
     * Clock is counting down for this player.
     */
    sealed class Running() : ClockState {
        protected abstract val endsAt: Instant

        override fun remainingTotalTime(): Duration = endsAt - Clock.System.now()
    }

    data class RunningBeforeFirstMove(override val settings: ClockSettings) : Running() {
        override val endsAt: Instant = Clock.System.now() + settings.startingTime + settings.extraTimeForFirstMove
        private val firstMoveTimeEndsAt: Instant = Clock.System.now() + settings.extraTimeForFirstMove

        override fun remainingTimeOnClock(): Duration {
            return minOf(settings.startingTime, endsAt - Clock.System.now())
        }

        override fun toPausedAfterMove() = Paused.fromRunningAfterMove(this)

        override fun toPausedWithoutMove() = Paused.fromRunningWithoutMove(this)

        override fun toRunning(): Running = this

        /**
         * The amount of remaining extra time that is given to a player before the first move.
         *
         * If the extra time has ended, [remainingTimeOnClock] starts to decrease, and this function
         * returns [Duration.ZERO].
         */
        fun remainingExtraTime() = maxOf(Duration.ZERO, firstMoveTimeEndsAt - Clock.System.now())
    }

    @ConsistentCopyVisibility
    data class RunningAfterFirstMove private constructor(
        override val settings: ClockSettings,
        override val endsAt: Instant
    ) : Running() {
        override fun remainingTimeOnClock() = endsAt - Clock.System.now()

        override fun toPausedAfterMove() = Paused.fromRunningAfterMove(this)

        override fun toPausedWithoutMove() = Paused.fromRunningWithoutMove(this)

        override fun toRunning() = this

        companion object {
            fun fromPaused(paused: Paused) = RunningAfterFirstMove(
                paused.settings,
                Clock.System.now() + paused.remainingTime
            )
        }
    }

    /**
     * Clock is not running for this player
     */
    @ConsistentCopyVisibility
    data class Paused private constructor(
        override val settings: ClockSettings,
        val remainingTime: Duration
    ) : ClockState {
        constructor(settings: ClockSettings) : this(
            settings,
            settings.startingTime
        )

        override fun remainingTimeOnClock(): Duration = remainingTime

        override fun remainingTotalTime(): Duration = remainingTime

        override fun toPausedAfterMove(): Paused = this

        override fun toPausedWithoutMove(): Paused = this

        override fun toRunning() = RunningAfterFirstMove.fromPaused(this)

        companion object {
            fun fromRunningAfterMove(running: Running) = Paused(
                running.settings,
                running.remainingTimeOnClock() + running.settings.moveIncrease
            )

            fun fromRunningWithoutMove(running: Running) = Paused(
                running.settings,
                running.remainingTimeOnClock()
            )
        }
    }
}
