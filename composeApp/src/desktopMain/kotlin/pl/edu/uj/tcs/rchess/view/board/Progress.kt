package pl.edu.uj.tcs.rchess.view.board

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.model.Draw
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Win
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import pl.edu.uj.tcs.rchess.model.state.GameState

@Composable
private fun ProgressRow(
    topText: String,
    bottomText: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            topText,
            style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
        )

        Text(
            bottomText,
            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
        )
    }
}

@Composable
fun ProgressFinished(result: GameResult) {
    when (result) {
        is Draw -> {
            // TODO: Human format
            ProgressRow("Draw", result.drawReason.name)
        }
        is Win -> {
            ProgressRow(
                when (result.winner) {
                    PlayerColor.WHITE -> "White won"
                    PlayerColor.BLACK -> "Black won"
                },
                // TODO: Human format
                result.winReason.name
            )
        }
    }
}

@Composable
fun ProgressRunning(
    currentTurn: PlayerColor,
) {
    ProgressRow(
        // TODO: Handle waiting for first move
        "Game in progress",
        when (currentTurn) {
            PlayerColor.WHITE -> "White turn"
            PlayerColor.BLACK -> "Black turn"
        }
    )
}

@Composable
fun Progress(
    gameState: GameState,
    currentBoardStateSelected: Boolean,
    onSelectCurrent: () -> Unit,
) {
    Box(
        Modifier.clickable(
            enabled = !currentBoardStateSelected,
            onClick = onSelectCurrent,
        )
    ) {
        when (gameState.progress) {
            is GameProgress.Finished -> ProgressFinished(gameState.progress.result)
            is GameProgress.Running -> ProgressRunning(gameState.currentState.currentTurn)
        }
    }
}
