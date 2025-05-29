package pl.edu.uj.tcs.rchess.view.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import pl.edu.uj.tcs.rchess.model.state.GameState
import pl.edu.uj.tcs.rchess.server.game.HistoryGame
import pl.edu.uj.tcs.rchess.view.theme.RandomChessTheme
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun GameWindowContent(game: HistoryGame) {
    RandomChessTheme {
        val gameState = remember {
            GameState.finished(
                initialBoardState = game.startingPosition, // TODO: Replace with initial board state from the database when implemented
                moves = game.moves,
                finishedProgress = GameProgress.Finished(
                    result = game.result
                ),
            )
        }

        GameScreen(gameState, null)
    }
}
