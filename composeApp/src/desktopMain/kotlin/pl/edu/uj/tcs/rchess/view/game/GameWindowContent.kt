package pl.edu.uj.tcs.rchess.view.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.edu.uj.tcs.rchess.server.game.ApiGame
import pl.edu.uj.tcs.rchess.server.game.HistoryGame
import pl.edu.uj.tcs.rchess.server.game.LiveGame
import pl.edu.uj.tcs.rchess.view.theme.RandomChessTheme

@Composable
fun GameWindowContent(game: ApiGame) {
    RandomChessTheme {
        val gameState by when (game) {
            is HistoryGame -> derivedStateOf { game.finalGameState }
            is LiveGame -> {
                game.controls.observer.stateFlow.collectAsStateWithLifecycle()
            }
        }
        val input = (game as? LiveGame)?.controls?.input

        GameScreen(gameState, input)
    }
}
