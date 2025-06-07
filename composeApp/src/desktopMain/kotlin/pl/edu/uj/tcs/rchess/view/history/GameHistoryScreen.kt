package pl.edu.uj.tcs.rchess.view.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.view.ImportPgnDialog
import pl.edu.uj.tcs.rchess.view.adapters.PagingAdapter
import pl.edu.uj.tcs.rchess.viewmodel.AppContext
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.icon_refresh

@Composable
fun GameHistoryScreen(context: AppContext) {
    val padding = 16.dp
    val bottomPadding = 32.dp

    val databaseState by context.clientApi.databaseState.collectAsState()

    var importPgnDialogVisible by remember { mutableStateOf(false) }

    if (importPgnDialogVisible) {
        ImportPgnDialog(
            context,
            onCancel = { importPgnDialogVisible = false },
            onSuccess = {
                context.gameHistoryViewModel.paging.refresh()
                importPgnDialogVisible = false
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = padding),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = padding),
            horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when {
                databaseState.synchronizing -> {
                    Text(
                        "Synchronizing games from external services...",
                        style = typography.bodySmall,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }

                databaseState.updatesAvailable && !context.gameHistoryViewModel.paging.initialLoading -> {
                    Text(
                        "Refresh to see latest changes",
                        style = typography.bodySmall,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }
            }

            TextButton(onClick = context.gameHistoryViewModel.paging::refresh) {
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

        PagingAdapter(
            context.gameHistoryViewModel.paging,
            "Loading game history...",
            PaddingValues(bottom = bottomPadding),
            listContent = { list ->
                items(list, key = { it::class to it.id }) { game ->
                    GameHistoryItem(
                        modifier = Modifier.fillMaxWidth(),
                        game = game,
                        onClick = {
                            context.navigation.openGameWindow(game)
                        }
                    )
                }
            },
            emptyListContent = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
                ) {
                    Text("No games imported yet", style = typography.displaySmall)
                    Text("Link a game service account or import a game manually", style = typography.bodyMedium)
                }
            },
        )
    }
}
