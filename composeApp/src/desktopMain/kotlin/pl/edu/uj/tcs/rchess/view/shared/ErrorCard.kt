package pl.edu.uj.tcs.rchess.view.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorCard(
    modifier: Modifier,
    headerText: String,
    error: Throwable,
    dismissText: String,
    onDismiss: () -> Unit,
    prominent: Boolean = false,
) {
    val verticalPadding = if (prominent) 24.dp else 12.dp

    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
        )
    ) {
        Row(Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(top = verticalPadding, bottom = verticalPadding + 4.dp)
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = headerText,
                    style = MaterialTheme.typography.titleLarge,
                )

                SelectionContainer(Modifier.padding(top = 8.dp)) {
                    Text(
                        text = error.message ?: "Unknown error",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Button(
                modifier = Modifier.padding(start = 16.dp).align(Alignment.CenterVertically),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                ),
                onClick = onDismiss,
            ) {
                Text(dismissText)
            }
        }
    }
}
