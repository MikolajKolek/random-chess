package pl.edu.uj.tcs.rchess.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.viewmodel.navigation.Route
import rchess.composeapp.generated.resources.*

@Composable
fun Sidebar(
    currentRoute: Route,
    onNavigate: (Route) -> Unit,
) {
    NavigationRail {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            NavigationRailItem(
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.icon_add),
                        contentDescription = "New game",
                    )
                },
                label = { Text("Play") },
                selected = currentRoute is Route.NewGame,
                onClick = { onNavigate(Route.NewGame) }
            )
            NavigationRailItem(
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.icon_game),
                        contentDescription = "Games",
                    )
                },
                label = { Text("Games") },
                selected = currentRoute is Route.GameHistory,
                onClick = { onNavigate(Route.GameHistory) }
            )
            NavigationRailItem(
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.icon_ranking),
                        contentDescription = "Ranking",
                    )
                },
                label = { Text("Ranking") },
                selected = currentRoute is Route.RankingList || currentRoute is Route.Ranking,
                onClick = { onNavigate(Route.RankingList) }
            )
            NavigationRailItem(
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.icon_tournament),
                        contentDescription = "Tournaments",
                    )
                },
                label = { Text("Tournaments") },
                selected = currentRoute is Route.TournamentList || currentRoute is Route.Tournament,
                onClick = { onNavigate(Route.TournamentList) }
            )
            Spacer(modifier = Modifier.weight(1f))
            NavigationRailItem(
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.icon_account),
                        contentDescription = "Account",
                    )
                },
                label = { Text("Account") },
                selected = currentRoute is Route.Account,
                onClick = { onNavigate(Route.Account) }
            )
        }
    }
}
