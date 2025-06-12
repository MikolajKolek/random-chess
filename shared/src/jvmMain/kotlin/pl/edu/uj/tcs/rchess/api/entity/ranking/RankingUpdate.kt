package pl.edu.uj.tcs.rchess.api.entity.ranking

/**
 * An update to two players' rankings after a game.
 *
 * If [blackEloUpdate] or [whiteEloUpdate] is null, that means
 * that the respective player is not included in the ranking at all, or their elo cannot update.
 */
data class RankingUpdate (
    val ranking: Ranking,
    val blackEloUpdate: EloUpdate?,
    val whiteEloUpdate: EloUpdate?
) {
    companion object
}