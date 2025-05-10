package pl.edu.uj.tcs.rchess.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import pl.edu.uj.tcs.rchess.server.HistoryGame

class NavigationManager {
    private val _route = mutableStateOf<Route>(Route.GameHistory)
    val route: Route
        get() = _route.value

    fun navigateTo(route: Route) {
        _route.value = route
    }

    private val _gameWindows = mutableStateListOf<HistoryGame>()
    val gameWindows
        get() = _gameWindows.toList()

    fun openGameWindow(game: HistoryGame) {
        _gameWindows.add(game)
    }

    fun closeGameWindow(index: Int) {
        _gameWindows.removeAt(index)
    }
}
