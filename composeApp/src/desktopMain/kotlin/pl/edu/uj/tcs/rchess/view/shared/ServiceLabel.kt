package pl.edu.uj.tcs.rchess.view.shared

import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import format
import pl.edu.uj.tcs.rchess.api.entity.Service

@Composable
fun ServiceLabel(service: Service?) {
    SuggestionChip(
        label = { Text(service.format()) },
        onClick = { /* TODO: Replace suggestion chip with a non-interactive component */ }
    )
}
