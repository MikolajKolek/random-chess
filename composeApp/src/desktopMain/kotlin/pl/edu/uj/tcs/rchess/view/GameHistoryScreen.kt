package pl.edu.uj.tcs.rchess.view

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.view.datastate.ErrorScreen
import pl.edu.uj.tcs.rchess.view.shared.Loading
import pl.edu.uj.tcs.rchess.viewmodel.AppContext
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.icon_refresh

@Composable
fun GameHistoryScreen(context: AppContext) {
    val padding = 16.dp
    val bottomPadding = 32.dp

    val scrollState = rememberLazyListState()
    // TODO: Accept requests only when the games are near
    val games by context.gameHistoryViewModel.paging.collectListAsState(derivedStateOf { true })
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

        if (games.isEmpty()) {
            val error = context.gameHistoryViewModel.paging.error
            if (error != null) {
                ErrorScreen(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    error = error,
                    onRetry = context.gameHistoryViewModel.paging::dismissError,
                )
            } else if (context.gameHistoryViewModel.paging.loading) {
                Loading(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    text = "Loading game history..."
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
                ) {
                    Text("No games imported yet", style = typography.displaySmall)
                    Text("Link a game service account or import a game manually", style = typography.bodyMedium)
                }
            }

            return@Column
        }

        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxHeight().weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = scrollState,
                contentPadding = PaddingValues(bottom = bottomPadding)
            ) {
                items(games, key = { it::class to it.id }) { game ->
                    GameHistoryItem(
                        modifier = Modifier.fillMaxWidth(),
                        game = game,
                        onClick = {
                            context.navigation.openGameWindow(game)
                        }
                    )
                }

                if (context.gameHistoryViewModel.paging.loading) {
                    item {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
            if (scrollState.canScrollForward || scrollState.canScrollBackward) {
                VerticalScrollbar(
                    modifier = Modifier.padding(start = 8.dp, bottom = bottomPadding).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(scrollState)
                )
            }
        }

        context.gameHistoryViewModel.paging.error?.let { error ->
            HorizontalDivider()

            ErrorScreen(
                modifier = Modifier.fillMaxWidth(),
                error = error,
                onRetry = context.gameHistoryViewModel.paging::dismissError,
            )
        }
    }
}
