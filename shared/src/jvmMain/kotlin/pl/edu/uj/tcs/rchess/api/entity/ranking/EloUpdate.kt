package pl.edu.uj.tcs.rchess.api.entity.ranking

data class EloUpdate(
    val oldElo: Int,
    val newElo: Int
)