package pl.edu.uj.tcs.rchess.viewmodel.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import pl.edu.uj.tcs.rchess.logger
import pl.edu.uj.tcs.rchess.server.game.ApiGame
import pl.edu.uj.tcs.rchess.server.game.HistoryGame
import pl.edu.uj.tcs.rchess.server.game.LiveGame

class NavigationManager {
    private val _route = mutableStateOf<Route>(Route.GameHistory)
    val route: Route
        get() = _route.value

    private val _newGameDialogVisible = mutableStateOf(false)
    val newGameDialogVisible: Boolean
        get() = _newGameDialogVisible.value

    fun navigateTo(route: Route) {
        _route.value = route
    }

    private val _gameWindows = mutableStateListOf<ApiGame>()
    val gameWindows
        get() = _gameWindows.toList()

    fun openGameWindow(game: ApiGame) {
        _gameWindows.add(game)
    }

    fun closeGameWindow(index: Int) {
        _gameWindows.removeAt(index)
    }

    /**
     * Replaces the [ApiGame] instance associated with the window at [index].
     * Used when a live game finishes.
     */
    fun replaceGameWindow(index: Int, liveGame: HistoryGame) {
        if (_gameWindows[index] !is LiveGame) {
            logger.warn { "Previous game replaced in replaceGameForWindow was not a LiveGame" }
        }
        _gameWindows[index] = liveGame
    }

    fun openNewGameDialog() {
        _newGameDialogVisible.value = true
    }

    fun closeNewGameDialog() {
        _newGameDialogVisible.value = false
    }
}
