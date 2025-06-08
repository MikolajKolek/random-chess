package pl.edu.uj.tcs.rchess.view.account

    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.layout.widthIn
    import androidx.compose.material3.Button
    import androidx.compose.material3.Card
    import androidx.compose.material3.CardDefaults
    import androidx.compose.material3.HorizontalDivider
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.key
    import androidx.compose.runtime.rememberCoroutineScope
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.platform.LocalUriHandler
    import androidx.compose.ui.unit.dp
    import kotlinx.coroutines.launch
    import pl.edu.uj.tcs.rchess.api.entity.Service
    import pl.edu.uj.tcs.rchess.util.logger
    import pl.edu.uj.tcs.rchess.view.shared.format
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
                logger.error(exception) { "Failed to link account" }
                // TODO: Handle error
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
                    modifier = Modifier.width(160.dp),
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
                        Row(
                            modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth()
                                .height(40.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                it.service.format(),
                                modifier = Modifier.widthIn(min = 160.dp),
                            )

                            Text(
                                it.displayName,
                            )
                        }
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