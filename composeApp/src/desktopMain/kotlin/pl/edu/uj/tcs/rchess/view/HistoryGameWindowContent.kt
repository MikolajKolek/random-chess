package pl.edu.uj.tcs.rchess.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import pl.edu.uj.tcs.rchess.model.GameOverReason
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import pl.edu.uj.tcs.rchess.model.state.GameState
import pl.edu.uj.tcs.rchess.server.HistoryGame
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun GameWindowContent(game: HistoryGame) {
    MaterialTheme {
        val gameState = remember {
            GameState.finished(
                initialBoardState = game.startingPosition, // TODO: Replace with initial board state from the database when implemented
                moves = game.moves,
                finishedProgress = GameProgress.Finished(
                    result = game.result,
                    // TODO: Use GameOverReason from the database when implemented
                    reason = GameOverReason.UNKNOWN,
                ),
            )
        }

        GameScreen(gameState, null)
    }
}
