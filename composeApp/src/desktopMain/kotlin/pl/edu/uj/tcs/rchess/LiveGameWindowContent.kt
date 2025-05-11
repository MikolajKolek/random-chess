package pl.edu.uj.tcs.rchess

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.edu.uj.tcs.rchess.components.GameScreen
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.game.LiveGame
import pl.edu.uj.tcs.rchess.model.game.LocalGameInput
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun LiveGameWindowContent(context: AppContext) {
    MaterialTheme {
        val coroutineScope = rememberCoroutineScope()

        // TODO: This is just for testing, replace with proper view model and live game source
        val (liveGame, gameInput) = remember {
            val liveGame = LiveGame()

            val playerGameInput = LocalGameInput(liveGame = liveGame, playerColor = PlayerColor.WHITE)
            val botGameInput = LocalGameInput(liveGame = liveGame, playerColor = PlayerColor.BLACK)

            val bot = context.config.bots.firstOrNull()!!.spawnBot()
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    bot.playGame(liveGame, botGameInput)
                }
            }

            LiveGame() to playerGameInput
        }

        val gameState by liveGame.stateFlow.collectAsStateWithLifecycle()

        GameScreen(gameState, gameInput)
    }
}
