package pl.edu.uj.tcs.rchess.view

import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import pl.edu.uj.tcs.rchess.server.Service

@Composable
fun ServiceLabel(service: Service?) {
    val name = when (service) {
        null -> "Imported"
        Service.RANDOM_CHESS -> "Random Chess"
        Service.CHESS_COM -> "Chess.com"
        Service.LICHESS -> "Lichess"
        Service.UNKNOWN -> "Unknown"
    }
    SuggestionChip(
        label = { Text(name) },
        onClick = { /* TODO: Replace suggestion chip with a non-interactive component */ }
    )
}
