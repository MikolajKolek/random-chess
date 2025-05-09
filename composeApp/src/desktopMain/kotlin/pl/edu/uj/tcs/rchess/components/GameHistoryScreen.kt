package pl.edu.uj.tcs.rchess.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import pl.edu.uj.tcs.rchess.server.ClientApi

@Composable
fun GameHistoryScreen(clientApi: ClientApi) {
    val viewModel = remember(clientApi) { GameHistoryViewModel(clientApi) }
    val gameHistory by viewModel.gameHistory.collectAsState()

    val gameHistoryNow = gameHistory
    when (gameHistoryNow) {
        is DataState.Error -> {
            Text("Error: ${gameHistoryNow.error.message}")
        }
        is DataState.Loading -> CircularProgressIndicator()
        is DataState.Success -> {
            Text(gameHistoryNow.data.toString())
        }
    }
}
