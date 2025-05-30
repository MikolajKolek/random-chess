package pl.edu.uj.tcs.rchess.view.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.edu.uj.tcs.rchess.server.game.ApiGame
import pl.edu.uj.tcs.rchess.server.game.HistoryGame
import pl.edu.uj.tcs.rchess.server.game.LiveGame
import pl.edu.uj.tcs.rchess.view.theme.RandomChessTheme
import pl.edu.uj.tcs.rchess.viewmodel.rememberGameWindowState
import java.awt.Dimension

@Composable
fun GameWindow(
    game: ApiGame,
    onCloseRequest: () -> Unit,
    onFinish: (historyGame: HistoryGame) -> Unit,

) {
    Window(
        onCloseRequest = onCloseRequest,
        title = when (game) {
            is HistoryGame -> "Random Chess history game"
            is LiveGame -> "Random Chess live game"
        },
        state = WindowState(
            placement = WindowPlacement.Maximized,
        ),
    ) {
        LaunchedEffect(game) {
            if (game !is LiveGame) return@LaunchedEffect
            val finishedGame = game.controls.observer.finishedGame.await()
            onFinish(finishedGame)
        }

        val gameState by when (game) {
            is HistoryGame -> derivedStateOf { game.finalGameState }
            is LiveGame -> {
                game.controls.observer.stateFlow.collectAsStateWithLifecycle()
            }
        }
        val input = (game as? LiveGame)?.controls?.input
        val windowState = rememberGameWindowState(gameState, input)

        window.minimumSize = Dimension(900, 600)
        RandomChessTheme {
            GameScreen(gameState, windowState)
        }
    }
}
