package pl.edu.uj.tcs.rchess

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.edu.uj.tcs.rchess.components.PlaceholderScreen
import pl.edu.uj.tcs.rchess.components.Sidebar
import pl.edu.uj.tcs.rchess.navigation.Route

// TODO: This is temporary
@Composable
fun Content(route: Route) {
    when (route) {
        is Route.NewGame -> PlaceholderScreen("New game")
        is Route.GameHistory -> PlaceholderScreen("Game history")
        is Route.RankingList -> PlaceholderScreen("Ranking list")
        is Route.TournamentList -> PlaceholderScreen("Tournament list")
        is Route.Account -> PlaceholderScreen("Account")
        is Route.Ranking -> PlaceholderScreen("Ranking ${route.rankingId}")
        is Route.Tournament -> PlaceholderScreen("Tournament ${route.tournamentId}")
    }
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        var route by remember { mutableStateOf<Route>(Route.NewGame) }

        Sidebar(
            route,
            onNavigate = { newRoute ->
                route = newRoute
            },
        )

        Content(route)
    }
}
