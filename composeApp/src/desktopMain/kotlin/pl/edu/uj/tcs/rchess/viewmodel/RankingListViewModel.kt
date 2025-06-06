package pl.edu.uj.tcs.rchess.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.viewmodel.datastate.DataStateViewModel

class RankingListViewModel(
    val clientApi: ClientApi,
): ViewModel() {
    val rankingList = DataStateViewModel { clientApi.getRankings() }

    private var _selectedRankingId by mutableStateOf<Int?>(null)
    val selectedRankingId: Int?
        get() = _selectedRankingId

    fun selectRanking(rankingId: Int) {
        _selectedRankingId = rankingId
    }
}
