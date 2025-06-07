package pl.edu.uj.tcs.rchess.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.viewmodel.navigation.Route
import rchess.composeapp.generated.resources.*

@Composable
fun Sidebar(
    currentRoute: Route,
    onNavigate: (Route) -> Unit,
    onOpenNewGameDialog: () -> Unit,
    demoProxyEnabled: Boolean? = null,
    onSetDemoProxyEnabled: (value: Boolean) -> Unit = {},
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

            demoProxyEnabled?.let {
                Column(
                    modifier = Modifier
                        .widthIn(max = 60.dp)
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "Simulate poor network",
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                    )
                    Switch(
                        checked = it,
                        onCheckedChange = onSetDemoProxyEnabled,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.errorContainer,
                            checkedThumbColor = MaterialTheme.colorScheme.error,
                            checkedBorderColor =  MaterialTheme.colorScheme.error,
                        ),
                    )
                }
            }

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
