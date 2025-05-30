package pl.edu.uj.tcs.rchess.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.view.datastate.DataStateScreen
import pl.edu.uj.tcs.rchess.viewmodel.AppContext
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.icon_refresh

@Composable
fun GameHistoryScreen(context: AppContext) {
    val padding = 16.dp

    DataStateScreen(context.gameListViewModel, "Loading game list") { games, refresh ->
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
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = padding),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = padding),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End),
                ) {
                    TextButton(onClick = refresh) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_refresh),
                            contentDescription = "Refresh",
                            modifier = Modifier.padding(end = 8.dp),
                        )

                        Text("Refresh")
                    }

                    Button(onClick = { importPgnDialogVisible = true }) {
                        Text("Import game")
                    }
                }

                // This layout is begging for a scrollbar, but as of May 2025
                // the Jetpack Compose team has yet to implement them.
                /// https://developer.android.com/jetpack/androidx/compose-roadmap
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(games) { game ->
                        GameHistoryItem(
                            modifier = Modifier.fillMaxWidth(),
                            game = game,
                            onClick = {
                                context.navigation.openGameWindow(game)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(padding + 16.dp))
                    }
                }
            }
        }
    }
}
