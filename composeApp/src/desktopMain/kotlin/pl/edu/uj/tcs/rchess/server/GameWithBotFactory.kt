package pl.edu.uj.tcs.rchess.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.edu.uj.tcs.rchess.config.BotType
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.game.LiveGame
import pl.edu.uj.tcs.rchess.model.game.PlayerGameControls
import kotlin.time.Duration.Companion.minutes

/**
 * A class for creating a live game with a bot.
 *
 * Implements the factory pattern.
 */
class GameWithBotFactory(
    private val botType: BotType,
) {
    fun createAndStart(
        playerColor: PlayerColor,
        coroutineScope: CoroutineScope,
    ): PlayerGameControls {
        val liveGame = LiveGame(
            timeLimit = 5.minutes
        )

        val playerGameInput = liveGame.getGameInput(playerColor = playerColor)
        val botGameInput = liveGame.getGameInput(playerColor = playerColor.opponent)

        val bot = botType.spawnBot()
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                bot.playGame(liveGame, botGameInput)
            }
        }

        return PlayerGameControls(
            observer = liveGame,
            input = playerGameInput,
        )
    }
}
