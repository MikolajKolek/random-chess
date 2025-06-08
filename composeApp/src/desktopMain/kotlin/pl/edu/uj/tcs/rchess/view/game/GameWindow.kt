package pl.edu.uj.tcs.rchess.view.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberWindowState
import pl.edu.uj.tcs.rchess.api.entity.game.ApiGame
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryGame
import pl.edu.uj.tcs.rchess.api.entity.game.LiveGame
import pl.edu.uj.tcs.rchess.view.theme.RandomChessTheme
import pl.edu.uj.tcs.rchess.viewmodel.rememberGameWindowState
import java.awt.Dimension

@Composable
fun GameWindow(
    game: ApiGame,
    icon: Painter,
    onCloseRequest: () -> Unit,
    onFinish: (historyGame: HistoryGame) -> Unit,
    onSelectRanking: (rankingId: Int) -> Unit,
) {
    LaunchedEffect(game) {
        if (game !is LiveGame) return@LaunchedEffect
        val finishedGame = game.controls.observer.finishedGame.await()
        onFinish(finishedGame)
    }

    val windowState = rememberGameWindowState(game)

    fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.type != KeyEventType.KeyDown) return false

        return when (event.key) {
            Key.DirectionLeft, Key.DirectionUp -> {
                windowState.boardStateBrowser.selectPrev()
                true
            }

            Key.DirectionRight, Key.DirectionDown -> {
                windowState.boardStateBrowser.selectNext()
                true
            }

            Key.MoveHome -> {
                windowState.boardStateBrowser.selectFirst()
                true
            }

            Key.MoveEnd -> {
                windowState.boardStateBrowser.selectLast()
                true
            }

            Key.R -> {
                windowState.flipOrientation()
                true
            }

            else -> false
        }
    }

    Window(
        onCloseRequest = onCloseRequest,
        onKeyEvent = {
            onKeyEvent(it)
        },
        title = when (game) {
            is HistoryGame -> "Random Chess history game"
            is LiveGame -> "Random Chess live game"
        },
        icon = icon,
        state = rememberWindowState(
            placement = WindowPlacement.Maximized,
            size = DpSize(1200.dp, 800.dp)
        ),
    ) {
        window.minimumSize = Dimension(900, 600)

        RandomChessTheme {
            GameScreen(windowState, onSelectRanking)
        }
    }
}
