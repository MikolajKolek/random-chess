package pl.edu.uj.tcs.rchess.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.api.Synchronized
import pl.edu.uj.tcs.rchess.api.Synchronizing
import pl.edu.uj.tcs.rchess.api.args.GamesRequestArgs
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryGame
import pl.edu.uj.tcs.rchess.util.logger
import pl.edu.uj.tcs.rchess.viewmodel.paging.PageFetchResult
import pl.edu.uj.tcs.rchess.viewmodel.paging.Paging
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

class GameHistoryViewModel(
    private val clientApi: ClientApi,
): ViewModel() {
    val paging = Paging(viewModelScope, ::fetchPage)

    /**
     * Requests a resync of games from external services and waits for the synchronization to finish.
     *
     * If the synchronization is taking more than a few seconds,
     * this function returns early to let the user see partial changes.
     */
    private suspend fun requestResyncAndWait() {
        val timeout = when (clientApi.databaseState.value.synchronizationState) {
            // When the user refreshes for the first time,
            // we give the server some time to sync service games.
            is Synchronized -> 5.seconds

            // If the user already knew that synchronization is in progress,
            // we can let them see partial changes quicker.
            is Synchronizing -> 1.seconds
        }
        val requestResyncTime = measureTime {
            clientApi.requestResync()
        }
        if (requestResyncTime > 1.seconds) {
            logger.warn { "Requesting a resync took $requestResyncTime" }
        }

        val timedOut = withTimeoutOrNull(timeout) {
            clientApi.databaseState.first { it.synchronizationState !is Synchronizing }
        } == null
        if (timedOut) {
            logger.info { "Waiting for sync to end exceeded timeout of $timeout, continuing" }
        }
    }

    private suspend fun fetchPage(key: HistoryGame?): PageFetchResult<HistoryGame, HistoryGame> {
        val isFirstPage = key == null
        if (isFirstPage) requestResyncAndWait()

        val requestedLength = 25
        val games = clientApi.getUserGames(
            GamesRequestArgs(
                after = key,
                length = requestedLength,
                // key == null indicates that this is the first page,
                // so we can clear the updatesAvailable indicator
                clearUpdatesAvailable = isFirstPage,
            )
        )
        return PageFetchResult(
            items = games,
            nextPageKey = games.lastOrNull()?.takeIf { games.size >= requestedLength },
        )
    }
}
