package pl.edu.uj.tcs.rchess.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.runBlocking
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.game.PlayerGameControls
import pl.edu.uj.tcs.rchess.viewmodel.AppContext
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun LiveGameWindowContent(context: AppContext) {
    MaterialTheme {
        val gameControls: PlayerGameControls = remember {
            // TODO: Create a ViewModel
            //  This is temporary.
            //  When a view model is implemented, runBlocking will no longer be used
            runBlocking {
                context.clientApi.startGameWithBot(PlayerColor.WHITE)
            }
        }

        val gameState by gameControls.observer.stateFlow.collectAsStateWithLifecycle()

        GameScreen(gameState, gameControls.input)
    }
}
