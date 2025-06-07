package pl.edu.uj.tcs.rchess.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryGame
import pl.edu.uj.tcs.rchess.viewmodel.paging.PageFetchResult
import pl.edu.uj.tcs.rchess.viewmodel.paging.Paging
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.time.Duration.Companion.milliseconds

class GameHistoryViewModel(
    private val clientApi: ClientApi,
): ViewModel() {
    val paging = Paging(viewModelScope, ::fetchPage)

    private suspend fun fetchPage(key: HistoryGame?): PageFetchResult<HistoryGame, HistoryGame> {
        // TODO: Remove debug code
        delay(500.milliseconds)
        if (Random.nextInt(0..<100) < 25) {
            throw Exception("Debug exception")
        }

        val requestedLength = 5 // TODO: Increase
        val games = clientApi.getUserGames(
            ClientApi.GamesRequestSettings(
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
