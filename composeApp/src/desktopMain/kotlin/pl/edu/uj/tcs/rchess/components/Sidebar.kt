package pl.edu.uj.tcs.rchess.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.edu.uj.tcs.rchess.navigation.NavigationViewModel
import pl.edu.uj.tcs.rchess.navigation.Route

@Composable
fun Sidebar(
    navigationViewModel: NavigationViewModel = viewModel(),
) = navigationViewModel.run {
    NavigationRail {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Play",
                    )
                },
                label = { Text("Play") },
                selected = route is Route.NewGame,
                onClick = { navigateTo(Route.NewGame) }
            )
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Games",
                    )
                },
                label = { Text("Games") },
                selected = route is Route.GameHistory,
                onClick = { navigateTo(Route.GameHistory) }
            )
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Ranking",
                    )
                },
                label = { Text("Ranking") },
                selected = route is Route.RankingList || route is Route.Ranking,
                onClick = { navigateTo(Route.RankingList) }
            )
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Tournaments",
                    )
                },
                label = { Text("Tournaments") },
                selected = route is Route.TournamentList || route is Route.Tournament,
                onClick = { navigateTo(Route.TournamentList) }
            )
            Spacer(modifier = Modifier.weight(1f))
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Account",
                    )
                },
                label = { Text("Account") },
                selected = route is Route.Account,
                onClick = { navigateTo(Route.Account) }
            )
        }
    }
}
