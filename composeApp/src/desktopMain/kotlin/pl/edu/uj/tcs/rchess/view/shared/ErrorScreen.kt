package pl.edu.uj.tcs.rchess.view.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ErrorScreen(modifier: Modifier, error: Throwable, onRetry: () -> Unit) {
    Column(
        modifier = modifier,
    ) {
        Text("Error: ${error.message}")
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
