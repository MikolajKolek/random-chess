package pl.edu.uj.tcs.rchess.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
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
    onOpenNewGameDialog: () -> Unit,
) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface,
        header = {
            Spacer(modifier = Modifier.height(16.dp))

            FloatingActionButton(
                onClick = {
                    onOpenNewGameDialog()
                },
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.icon_start_game),
                    contentDescription = "New game",
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
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
                        contentDescription = "Rankings",
                    )
                },
                label = { Text("Ranking") },
                selected = currentRoute is Route.Rankings || currentRoute is Route.Ranking,
                onClick = { onNavigate(Route.Rankings) }
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
