package pl.edu.uj.tcs.rchess.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import pl.edu.uj.tcs.rchess.server.ClientApi

@Composable
fun GameHistoryScreen(clientApi: ClientApi) {
    val viewModel = remember(clientApi) { GameHistoryViewModel(clientApi) }
    val gameHistory by viewModel.gameHistory.collectAsState()

    val gameHistoryNow = gameHistory
    when (gameHistoryNow) {
        is DataState.Loading -> CircularProgressIndicator()
        is DataState.Error -> {
            Column {
                Text("Error: ${gameHistoryNow.error.message}")
                Button(onClick = viewModel::refresh) {
                    Text("Refresh")
                }
            }
        }
        is DataState.Success -> {
            Button(onClick = viewModel::refresh) {
                Text("Refresh")
            }

            if (gameHistoryNow.data.isEmpty()) {
                Text("No games found")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(gameHistoryNow.data) { game ->
                        GameHistoryItem(game = game, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}
