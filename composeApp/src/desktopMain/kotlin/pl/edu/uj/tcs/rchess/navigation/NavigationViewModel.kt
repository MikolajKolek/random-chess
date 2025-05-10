package pl.edu.uj.tcs.rchess.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class NavigationViewModel : ViewModel() {
    private val _route = mutableStateOf<Route>(Route.GameHistory)
    val route: Route
        get() = _route.value

    fun navigateTo(route: Route) {
        _route.value = route
    }
}
