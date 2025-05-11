package pl.edu.uj.tcs.rchess

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import pl.edu.uj.tcs.rchess.components.GameScreen
import pl.edu.uj.tcs.rchess.model.GameOverReason
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import pl.edu.uj.tcs.rchess.model.state.ImmutableGameState
import pl.edu.uj.tcs.rchess.server.HistoryGame
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun GameWindowContent(game: HistoryGame) {
    MaterialTheme {
        val gameState = remember {
            ImmutableGameState.finished(
                initialBoardState = game.startingPosition, // TODO: Replace with initial board state from the database when implemented
                moves = game.moves,
                finishedProgress = GameProgress.Finished(
                    result = game.result,
                    // TODO: Use GameOverReason from the database when implemented
                    reason = GameOverReason.UNKNOWN,
                ),
            )
        }

        Text(game.id.toString())

        GameScreen(gameState, null)
    }
}
