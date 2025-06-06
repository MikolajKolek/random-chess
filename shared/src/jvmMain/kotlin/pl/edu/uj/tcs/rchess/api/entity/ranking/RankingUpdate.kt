package pl.edu.uj.tcs.rchess.api.entity.ranking

data class RankingUpdate (
    val ranking: Ranking,
    val blackEloUpdate: EloUpdate?,
    val whiteEloUpdate: EloUpdate?
) {
    companion object
}