package pl.edu.uj.tcs.rchess.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.api.args.RankingRequestArgs
import pl.edu.uj.tcs.rchess.api.entity.ranking.RankingSpot
import pl.edu.uj.tcs.rchess.viewmodel.datastate.DataStateViewModel
import pl.edu.uj.tcs.rchess.viewmodel.paging.PageFetchResult
import pl.edu.uj.tcs.rchess.viewmodel.paging.Paging
import java.time.OffsetDateTime

class RankingListViewModel(
    val clientApi: ClientApi,
): ViewModel() {
    val rankingList = DataStateViewModel { clientApi.getRankingsList() }

    private val rankingToPaging = mutableStateMapOf<Int, Paging<RankingSpot>>()

    private fun createPaging(rankingId: Int): Paging<RankingSpot> {
        return Paging<RankingSpot, RankingKey>(viewModelScope) { key ->
            val requestedLength = 50
            val settings = RankingRequestArgs(
                rankingId,
                after = key?.lastSpot,
                length = requestedLength,
                atTimestamp = key?.atTimestamp ?: OffsetDateTime.now(),
            )
            val spots = clientApi.getRankingPlacements(settings)
            return@Paging PageFetchResult(
                items = spots,
                nextPageKey = spots.lastOrNull()?.takeIf { spots.size >= requestedLength }?.let {
                    RankingKey(
                        it,
                        settings.atTimestamp,
                    )
                },
            )
        }
    }

    fun getRankingPaging(rankingId: Int): Paging<RankingSpot> =
        rankingToPaging.getOrPut(rankingId) {
            createPaging(rankingId)
        }

    private data class RankingKey(
        val lastSpot: RankingSpot?,
        val atTimestamp: OffsetDateTime,
    )

    fun refreshAll() {
        rankingToPaging.values.forEach { paging ->
            paging.refresh()
        }
    }
}
