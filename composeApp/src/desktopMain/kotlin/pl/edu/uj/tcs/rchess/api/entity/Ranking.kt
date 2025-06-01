package pl.edu.uj.tcs.rchess.api.entity

import kotlin.time.Duration

/**
 * Data class representing a ranking with its configuration.
 */
data class Ranking(
    val id: Int,
    val name: String,
    val playtimeMin: Duration,
    val playtimeMax: Duration?,
    val extraMoveMultiplier: Int,
    val includeBots: Boolean,
)
