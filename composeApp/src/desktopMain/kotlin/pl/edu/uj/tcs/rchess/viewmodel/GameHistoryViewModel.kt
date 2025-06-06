package pl.edu.uj.tcs.rchess.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryGame
import pl.edu.uj.tcs.rchess.util.logger
import pl.edu.uj.tcs.rchess.utils.waitUntil
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.time.Duration.Companion.seconds

class GameHistoryViewModel(
    private val clientApi: ClientApi,
): ViewModel() {
    private var _error by mutableStateOf<Exception?>(null)
    private var _loading by mutableStateOf(false)
    private val _list = mutableStateListOf<HistoryGame>()
    private var reachedEnd = false

    private val acceptingRequestStates = mutableStateListOf<State<Boolean>>()

    init {
        viewModelScope.launch {
            flow {
                while (true) {
                    waitUntil {
                        _error == null && !reachedEnd && acceptingRequestStates.any { it.value }
                    }

                    try {
                        _loading = true

                        // TODO: Remove debug code
                        delay(3.seconds)
                        if (Random.nextInt(0..<100) >= 40) {
                            throw Exception("Debug exception")
                        }

                        val requestedLength = 5 // TODO: Increase
                        val games = clientApi.getUserGames(
                            ClientApi.GamesRequestSettings(
                                after = _list.lastOrNull(),
                                length = requestedLength,
                                refreshAvailableUpdates = _list.isEmpty()
                            )
                        )
                        if (games.size < requestedLength) reachedEnd = true
                        emitAll(games.asFlow())
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                        logger.error(e) {  "Error while fetching game history items" }
                        _error = e
                    } finally {
                        _loading = false
                    }
                }
            }.toList(_list)
        }
    }

    val error: Exception?
        get() = _error

    val loading: Boolean
        get() = _loading

    fun refresh() {
        // TODO
    }

    fun dismissError() {
        _error = null
    }

    @Composable
    fun collectListAsState(acceptingRequests: State<Boolean>): State<List<HistoryGame>> {
        DisposableEffect(acceptingRequests) {
            acceptingRequestStates.add(acceptingRequests)

            onDispose {
                acceptingRequestStates.remove(acceptingRequests)
            }
        }

        return remember { derivedStateOf { _list.toList() } }
    }

    val databaseState: ClientApi.DatabaseState
        get() = clientApi.databaseState.value
}
