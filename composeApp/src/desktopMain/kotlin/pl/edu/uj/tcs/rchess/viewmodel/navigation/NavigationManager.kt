package pl.edu.uj.tcs.rchess.viewmodel.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.awt.ComposeWindow
import pl.edu.uj.tcs.rchess.api.entity.game.ApiGame
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryGame
import pl.edu.uj.tcs.rchess.api.entity.game.LiveGame
import pl.edu.uj.tcs.rchess.util.logger

class NavigationManager {
    private val _route = mutableStateOf<Route>(Route.GameHistory)
    val route: Route
        get() = _route.value

    private val _newGameDialogVisible = mutableStateOf(false)
    val newGameDialogVisible: Boolean
        get() = _newGameDialogVisible.value

    private var mainWindow: ComposeWindow? = null

    fun navigateTo(route: Route) {
        _route.value = route
        mainWindow?.run {
            try {
                toFront()
                requestFocus()
            } catch (exception: Exception) {
                logger.error(exception) { "Failed to focus on main window" }
            }
        }
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
    fun replaceGameWindow(index: Int, historyGame: HistoryGame) {
        if (_gameWindows[index] !is LiveGame) {
            logger.warn { "Previous game replaced in replaceGameForWindow was not a LiveGame" }
        }
        _gameWindows[index] = historyGame
    }

    fun openNewGameDialog() {
        _newGameDialogVisible.value = true
    }

    fun closeNewGameDialog() {
        _newGameDialogVisible.value = false
    }

    fun storeMainWindowReference(window: ComposeWindow) {
        mainWindow = window
    }
}
