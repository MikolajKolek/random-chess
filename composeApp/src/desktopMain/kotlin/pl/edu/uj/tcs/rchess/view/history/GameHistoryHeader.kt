package pl.edu.uj.tcs.rchess.view.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.api.DatabaseState
import pl.edu.uj.tcs.rchess.api.Synchronized
import pl.edu.uj.tcs.rchess.api.Synchronizing
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.icon_refresh

@Composable
fun GameHistoryHeader(
    modifier: Modifier,
    databaseState: DatabaseState,
    initialLoading: Boolean,
    onRefresh: () -> Unit,
    onImportClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when {
            databaseState.synchronizationState is Synchronizing -> {
                Text(
                    "Synchronizing games from external services...",
                    style = typography.bodySmall,
                    modifier = Modifier.padding(end = 8.dp),
                )
            }

            (databaseState.synchronizationState as? Synchronized)?.errors?.isNotEmpty() == true -> {
                Text(
                    "Encountered an error while synchronizing games",
                    style = typography.bodySmall,
                    modifier = Modifier.padding(end = 8.dp),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            databaseState.updatesAvailable && !initialLoading -> {
                Surface(
                    modifier = Modifier.padding(end = 8.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        "Refresh to see latest changes",
                        style = typography.bodySmall,
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                    )
                }
            }
        }

        TextButton(onClick = onRefresh) {
            Icon(
                painter = painterResource(Res.drawable.icon_refresh),
                contentDescription = "Refresh",
                modifier = Modifier.padding(end = 8.dp),
            )

            Text("Refresh")
        }

        Button(onClick = onImportClick) {
            Text("Import game")
        }
    }
}
