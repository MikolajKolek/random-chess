package pl.edu.uj.tcs.rchess.components.datastate

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ErrorScreen(error: Throwable, onRefresh: () -> Unit) {
    Column {
        Text("Error: ${error.message}")
        Button(onClick = onRefresh) {
            Text("Refresh")
        }
    }
}
