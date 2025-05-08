package pl.edu.uj.tcs.rchess.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Sidebar() {
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
                selected = false,
                onClick = { /* TODO */ }
            )
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Games",
                    )
                },
                label = { Text("Games") },
                selected = false,
                onClick = { /* TODO */ }
            )
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Ranking",
                    )
                },
                label = { Text("Ranking") },
                selected = false,
                onClick = { /* TODO */ }
            )
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Tournaments",
                    )
                },
                label = { Text("Tournaments") },
                selected = false,
                onClick = { /* TODO */ }
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
                selected = false,
                onClick = { /* TODO */ }
            )
        }
    }
}
