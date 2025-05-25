package pl.edu.uj.tcs.rchess.model

import kotlin.time.Duration

data class ClockSettings(
    val startingTime: Duration,
    val moveIncrease: Duration,
    val extraTimeForFirstMove: Duration,
)