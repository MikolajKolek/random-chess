package pl.edu.uj.tcs.rchess.view.account

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pl.edu.uj.tcs.rchess.api.entity.Service
import pl.edu.uj.tcs.rchess.util.logger
import pl.edu.uj.tcs.rchess.viewmodel.AppContext

@Composable
fun AccountScreen(context: AppContext) {
    val serviceAccounts by context.clientApi.serviceAccounts.collectAsState()

    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()

    fun linkLichess() {
        coroutineScope.launch {
            try {
                val response = context.clientApi.addExternalAccount(Service.LICHESS)
                uriHandler.openUri(response.oAuthUrl)
                response.completionCallback.await()
            } catch (exception: Exception) {
                // If a linking dialog is implemented, we should catch errors here,
                // to show them in the dialog window.
                logger.error(exception) { "Failed to link account" }
            }
        }
    }

    Box(
        modifier = Modifier.padding(all = 16.dp).fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
        ) {
            Text(
                "Connected accounts",
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp),
                style = MaterialTheme.typography.titleLarge,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                Text(
                    "Service",
                    modifier = Modifier.width(216.dp),
                    style = MaterialTheme.typography.labelLarge,
                )

                Text(
                    "Account name",
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            HorizontalDivider()

            Column {
                serviceAccounts.forEach {
                    key(it.service to it.userIdInService) {
                        AccountListItem(it)
                    }
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.padding(all = 8.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                Button(
                    onClick = {
                        linkLichess()
                    },
                ) {
                    Text("Link Lichess account")
                }
            }
        }
    }
}
