package pl.edu.uj.tcs.rchess.api.entity.ranking

import kotlin.time.Duration

/**
 * Data class representing a ranking with its configuration.
 */
data class Ranking(
    val id: Int,
    val name: String,
    val playtimeMin: Duration,
    /**
     * If the ranking doesn't have an upper playtime limit,
     * playtimeMax is equal to [Duration.INFINITE].
     */
    val playtimeMax: Duration,
    val extraMoveMultiplier: Int,
    val includeBots: Boolean,
)
