package pl.edu.uj.tcs.rchess

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.edu.uj.tcs.rchess.components.GameHistoryScreen
import pl.edu.uj.tcs.rchess.components.PlaceholderScreen
import pl.edu.uj.tcs.rchess.components.Sidebar
import pl.edu.uj.tcs.rchess.navigation.NavigationViewModel
import pl.edu.uj.tcs.rchess.navigation.Route
import pl.edu.uj.tcs.rchess.server.ClientApi

// TODO: This is temporary
@Composable
fun RouteScreen(
    route: Route,
    clientApi: ClientApi,
) {
    when (route) {
        is Route.NewGame -> PlaceholderScreen("New game")
        is Route.GameHistory -> GameHistoryScreen(clientApi)
        is Route.RankingList -> PlaceholderScreen("Ranking list")
        is Route.TournamentList -> PlaceholderScreen("Tournament list")
        is Route.Account -> PlaceholderScreen("Account")
        is Route.Ranking -> PlaceholderScreen("Ranking ${route.rankingId}")
        is Route.Tournament -> PlaceholderScreen("Tournament ${route.tournamentId}")
    }
}

@Composable
@Preview
fun App(
    clientApi: ClientApi,
    navigationViewModel: NavigationViewModel = viewModel(),
) {
    MaterialTheme {
        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
            Sidebar()

            Box(
                modifier = Modifier.fillMaxSize().widthIn(max = 600.dp),
            ) {
                RouteScreen(navigationViewModel.route, clientApi)
            }
        }
    }
}
