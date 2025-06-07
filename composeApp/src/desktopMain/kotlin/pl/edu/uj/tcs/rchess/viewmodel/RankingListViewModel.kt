package pl.edu.uj.tcs.rchess.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.api.entity.ranking.RankingSpot
import pl.edu.uj.tcs.rchess.viewmodel.datastate.DataStateViewModel
import pl.edu.uj.tcs.rchess.viewmodel.paging.PageFetchResult
import pl.edu.uj.tcs.rchess.viewmodel.paging.Paging
import java.time.LocalDateTime

class RankingListViewModel(
    val clientApi: ClientApi,
): ViewModel() {
    val rankingList = DataStateViewModel { clientApi.getRankingsList() }

    private var _selectedRankingId by mutableStateOf<Int?>(null)
    val selectedRankingId: Int?
        get() = _selectedRankingId

    fun selectRanking(rankingId: Int) {
        _selectedRankingId = rankingId
    }

    private val rankingToPaging = mutableStateMapOf<Int, Paging<RankingSpot>>()

    private fun createPaging(rankingId: Int): Paging<RankingSpot> {
        return Paging<RankingSpot, RankingKey>(viewModelScope) { key ->
            val requestedLength = 50
            val settings = ClientApi.RankingRequestSettings(
                rankingId,
                after = key?.lastSpot,
                length = requestedLength,
                atTimestamp = key?.atTimestamp ?: LocalDateTime.now(),
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

    val selectedRankingPaging: Paging<RankingSpot>?
        get() = _selectedRankingId?.let {
            rankingToPaging.getOrPut(it) {
                createPaging(it)
            }
        }

    private data class RankingKey(
        val lastSpot: RankingSpot?,
        val atTimestamp: LocalDateTime,
    )

    fun refreshAll() {
        rankingToPaging.values.forEach { paging ->
            paging.refresh()
        }
    }
}
