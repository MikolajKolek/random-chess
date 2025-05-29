package pl.edu.uj.tcs.rchess.viewmodel.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import pl.edu.uj.tcs.rchess.server.game.ApiGame

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

    fun openNewGameDialog() {
        _newGameDialogVisible.value = true
    }

    fun closeNewGameDialog() {
        _newGameDialogVisible.value = false
    }
}
