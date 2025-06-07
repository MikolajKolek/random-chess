package pl.edu.uj.tcs.rchess.view.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.view.ImportPgnDialog
import pl.edu.uj.tcs.rchess.view.adapters.PagingAdapter
import pl.edu.uj.tcs.rchess.viewmodel.AppContext

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
        GameHistoryHeader(
            Modifier.fillMaxWidth().padding(vertical = padding),
            databaseState,
            initialLoading = context.gameHistoryViewModel.paging.initialLoading,
            onRefresh = context.gameHistoryViewModel.paging::refresh,
            onImportClick = { importPgnDialogVisible = true },
        )

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
