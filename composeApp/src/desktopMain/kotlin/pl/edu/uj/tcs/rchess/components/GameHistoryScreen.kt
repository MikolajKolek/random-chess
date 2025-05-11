package pl.edu.uj.tcs.rchess.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.AppContext
import pl.edu.uj.tcs.rchess.components.datastate.DataStateScreen

@Composable
fun GameHistoryScreen(context: AppContext) {
    DataStateScreen(
        { context.clientApi.getUserGames() }
    ) { games, refresh ->
        var importPgnDialogVisible by remember { mutableStateOf(false) }

        if (importPgnDialogVisible) {
            ImportPgnDialog(
                context,
                onCancel = { importPgnDialogVisible = false },
                onSuccess = {
                    refresh()
                    importPgnDialogVisible = false
                }
            )
        }

        if (games.isEmpty()) {
            Column {
                Text("No games found")
                Button(onClick = { importPgnDialogVisible = true }) {
                    Text("Import game")
                }
                Button(onClick = refresh) {
                    Text("Refresh")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Button(onClick = { importPgnDialogVisible = true }) {
                        Text("Import game")
                    }
                }

                item {
                    Button(onClick = refresh) {
                        Text("Refresh")
                    }
                }

                items(games) { game ->
                    GameHistoryItem(
                        modifier = Modifier.fillMaxWidth(),
                        game = game,
                        onClick = {
                            context.navigation.openGameWindow(game)
                        }
                    )
                }
            }
        }
    }
}
