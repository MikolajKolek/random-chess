package pl.edu.uj.tcs.rchess.view.game

import androidx.compose.runtime.Composable
import pl.edu.uj.tcs.rchess.model.state.GameState
import pl.edu.uj.tcs.rchess.server.game.ApiGame
import pl.edu.uj.tcs.rchess.server.game.HistoryGame
import pl.edu.uj.tcs.rchess.server.game.LiveGame
import pl.edu.uj.tcs.rchess.view.theme.RandomChessTheme
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun GameWindowContent(game: ApiGame) {
    RandomChessTheme {
        val gameState: GameState = when (game) {
            is HistoryGame -> game.finalGameState
            is LiveGame -> game.controls.observer.stateFlow.value
        }
        val input = (game as? LiveGame)?.controls?.input

        GameScreen(gameState, input)
    }
}
