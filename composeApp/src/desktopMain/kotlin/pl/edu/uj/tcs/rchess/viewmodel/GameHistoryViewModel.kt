package pl.edu.uj.tcs.rchess.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.api.args.GamesRequestArgs
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryGame
import pl.edu.uj.tcs.rchess.viewmodel.paging.PageFetchResult
import pl.edu.uj.tcs.rchess.viewmodel.paging.Paging

class GameHistoryViewModel(
    private val clientApi: ClientApi,
): ViewModel() {
    val paging = Paging(viewModelScope, ::fetchPage)

    private suspend fun fetchPage(key: HistoryGame?): PageFetchResult<HistoryGame, HistoryGame> {
        val requestedLength = 25
        val games = clientApi.getUserGames(
            GamesRequestArgs(
                after = key,
                length = requestedLength,
                // key == null indicates that this is the first page,
                // so we can clear the updatesAvailable indicator
                refreshAvailableUpdates = key == null,
            )
        )
        return PageFetchResult(
            items = games,
            nextPageKey = games.lastOrNull()?.takeIf { games.size >= requestedLength },
        )
    }
}
