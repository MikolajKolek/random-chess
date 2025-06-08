package pl.edu.uj.tcs.rchess.view.gamesidebar

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import formatReason
import formatResult
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.PlayerColor
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
private fun ProgressFinished(result: GameResult) {
    ProgressRow(
        result.formatResult(),
        result.formatReason(),
    )
}

@Composable
private fun ProgressRunning(
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

    val (backgroundColor, contentColor) = when {
            !waitingForOwnMove -> Color.White to Color.Unspecified

            currentBoardStateSelected -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer

            else -> infiniteTransition.animateColor(
                targetValue = Color.White,
                initialValue = MaterialTheme.colorScheme.tertiaryContainer,
                animationSpec = infiniteRepeatable(
                    animation = tween(350, delayMillis = 300, easing = EaseInOut),
                    repeatMode = RepeatMode.Reverse,
                ),
            ).value to MaterialTheme.colorScheme.onTertiaryContainer
    }

    Surface(
        Modifier
            .clickable(
                enabled = !currentBoardStateSelected,
                onClick = onSelectCurrent,
            ),
        color = backgroundColor,
        contentColor = contentColor,
    ) {
        gameState.progress.let {
            when (it) {
                is GameProgress.Finished -> ProgressFinished(it.result)
                is GameProgress.Running -> ProgressRunning(gameState.currentState.currentTurn)
            }
        }
    }
}
