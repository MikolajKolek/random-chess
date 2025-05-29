package pl.edu.uj.tcs.rchess.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.edu.uj.tcs.rchess.config.BotType
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.game.LiveGameController
import pl.edu.uj.tcs.rchess.model.game.PlayerGameControls
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

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
        val liveGame = LiveGameController(
            //TODO: add more clock options
            clockSettings = ClockSettings(
                startingTime = 5.minutes,
                moveIncrease = 3.seconds,
                extraTimeForFirstMove = 20.seconds
            ),
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
