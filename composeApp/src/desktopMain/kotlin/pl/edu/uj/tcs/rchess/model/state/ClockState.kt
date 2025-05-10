package pl.edu.uj.tcs.rchess.model.state

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

//TODO: implement time increase on move?
// if it is implemented also implement winc and binc in BotClockState
abstract class ClockState {
    abstract val totalTime: Duration

    abstract fun remainingTime() : Duration

    // TODO: In a client-server scenario, a time synchronization mechanism should be implemented,
    //  Instant might not be the best choice, as we cannot trust the the client's system clock to
    //  be the same as the server's.
    @OptIn(ExperimentalTime::class)
    /**
     * Clock is counting down for this player, the remaining duration can be calculated using [endsAt]
     */
    data class Running(override val totalTime: Duration, val endsAt: Instant) : ClockState() {
        override fun remainingTime(): Duration {
            return endsAt - Clock.System.now()
        }
    }

    /**
     * Clock is not running for this player
     */
    data class Paused(override val totalTime: Duration, val remainingTime: Duration) : ClockState() {
        override fun remainingTime(): Duration {
            return remainingTime
        }
    }
}
