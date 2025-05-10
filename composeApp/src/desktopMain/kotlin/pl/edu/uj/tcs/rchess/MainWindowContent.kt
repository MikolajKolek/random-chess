package pl.edu.uj.tcs.rchess

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.edu.uj.tcs.rchess.components.GameHistoryScreen
import pl.edu.uj.tcs.rchess.components.PlaceholderScreen
import pl.edu.uj.tcs.rchess.components.Sidebar
import pl.edu.uj.tcs.rchess.navigation.Route

// TODO: This is temporary
@Composable
fun RouteScreen(
    context: AppContext,
) {
    context.navigation.route.let { route ->
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
}

@Composable
@Preview
fun MainWindowContent(
    context: AppContext,
) {
    MaterialTheme {
        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
            Sidebar(context.navigation.route, context.navigation::navigateTo)

            Box(
                modifier = Modifier.fillMaxSize().widthIn(max = 600.dp),
            ) {
                RouteScreen(context)
            }
        }
    }
}
