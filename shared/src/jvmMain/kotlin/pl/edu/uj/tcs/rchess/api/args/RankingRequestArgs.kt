package pl.edu.uj.tcs.rchess.api.args

import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.api.entity.ranking.RankingSpot
import java.time.OffsetDateTime

data class RankingRequestArgs(
    /**
     * The id of the ranking that is being queried.
     */
    val rankingId: Int,
    /**
     * Return games that are after the given [ServiceAccount] in the game list.
     *
     * If this is null, the request returns entries starting from the top of the ranking.
     */
    val after: RankingSpot? = null,
    /**
     * The number of ranking entries that should be returned.
     */
    val length: Int = 100,
    /**
     * The state of the rankings will be queried at this timestamp. This is useful
     * for displaying a consistent infinite-scrolling ranking guaranteed
     * not to display conflicting information.
     */
    val atTimestamp: OffsetDateTime = OffsetDateTime.now()
)