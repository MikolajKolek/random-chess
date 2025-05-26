package pl.edu.uj.tcs.rchess.view.gamesidebar

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import format
import formatCapitalized
import pl.edu.uj.tcs.rchess.model.Draw
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Win
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import pl.edu.uj.tcs.rchess.model.state.GameState
import pl.edu.uj.tcs.rchess.util.runIf

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
            ProgressRow("Draw", result.drawReason.format())
        }

        is Win -> {
            ProgressRow(
                "${result.winner.formatCapitalized()} won",
                result.winReason.format(result.winner)
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
    waitingForOwnMove: Boolean,
) {
    val infiniteTransition = rememberInfiniteTransition()

    Box(
        Modifier
            .clickable(
                enabled = !currentBoardStateSelected,
                onClick = onSelectCurrent,
            )
            .background(Color.White)
            .runIf(waitingForOwnMove) {
                val color = if (currentBoardStateSelected)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    // Highlight when the player is browsing an earlier move,
                    // while the game is running and it's their turn
                    infiniteTransition.animateColor(
                        targetValue = Color.White,
                        initialValue = MaterialTheme.colorScheme.primaryContainer,
                        animationSpec = infiniteRepeatable(
                            animation = tween(500, delayMillis = 500, easing = EaseInOut),
                            repeatMode = RepeatMode.Reverse,
                        ),
                    ).value

                background(color)
            }
    ) {
        when (gameState.progress) {
            is GameProgress.Finished -> ProgressFinished(gameState.progress.result)
            is GameProgress.Running -> ProgressRunning(gameState.currentState.currentTurn)
        }
    }
}
