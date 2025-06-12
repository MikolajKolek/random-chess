package pl.edu.uj.tcs.rchess.api.entity.ranking

/**
 * An update to an elo value.
 */
data class EloUpdate(
    val oldElo: Int,
    val newElo: Int
)