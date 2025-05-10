package pl.edu.uj.tcs.rchess.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.components.datastate.DataStateScreen
import pl.edu.uj.tcs.rchess.server.ClientApi

@Composable
fun GameHistoryScreen(clientApi: ClientApi) {
    DataStateScreen(
        { clientApi.getUserGames() }
    ) { games, refresh ->
        if (games.isEmpty()) {
            Text("No games found")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Button(onClick = refresh) {
                        Text("Refresh")
                    }
                }

                items(games) { game ->
                    GameHistoryItem(game = game, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}
