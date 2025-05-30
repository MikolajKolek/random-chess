package pl.edu.uj.tcs.rchess.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class ClockSettings(
    val startingTime: Duration,
    val moveIncrease: Duration,
    val extraTimeForFirstMove: Duration = 30.seconds,
)