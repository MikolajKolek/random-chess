package pl.edu.uj.tcs.rchess.view.datastate

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ErrorScreen(error: Throwable, onRefresh: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text("Error: ${error.message}")
        Button(onClick = onRefresh) {
            Text("Refresh")
        }
    }
}
