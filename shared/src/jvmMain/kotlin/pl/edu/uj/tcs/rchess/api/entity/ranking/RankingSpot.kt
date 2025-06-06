package pl.edu.uj.tcs.rchess.api.entity.ranking

import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount

data class RankingSpot (
    val placement: Int,
    val serviceAccount: ServiceAccount,
    val elo: Int
)