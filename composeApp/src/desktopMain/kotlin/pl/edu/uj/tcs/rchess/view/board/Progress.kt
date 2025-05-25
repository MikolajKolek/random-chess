package pl.edu.uj.tcs.rchess.view.board

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.model.Draw
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Win
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import pl.edu.uj.tcs.rchess.model.state.GameState

@Composable
fun Progress(gameState: GameState) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val (topRow, bottomRow) = when (gameState.progress) {
            is GameProgress.Finished -> {
                when (gameState.progress.result) {
                    is Draw -> {
                        "Draw" to
                        // TODO: Human format
                        gameState.progress.result.drawReason.name
                    }

                    is Win -> {
                        when (gameState.progress.result.winner) {
                            PlayerColor.WHITE -> "White won"
                            PlayerColor.BLACK -> "Black won"
                        } to
                        // TODO: Human format
                        gameState.progress.result.winReason.name
                    }
                }
            }

            is GameProgress.Running -> {
                "Game in progress" to
                // TODO: Handle waiting for first move
                when (gameState.currentState.currentTurn) {
                    PlayerColor.WHITE -> "White turn"
                    PlayerColor.BLACK -> "Black turn"
                }
            }
        }

        Text(
            topRow,
            style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
        )

        Text(
            bottomRow,
            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
        )
    }
}
