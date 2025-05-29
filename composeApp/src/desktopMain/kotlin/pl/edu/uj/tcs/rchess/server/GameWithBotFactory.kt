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

/**
 * A class for creating a live game with a bot.
 *
 * Implements the factory pattern.
 */
class GameWithBotFactory(
    private val database: Database
) {
    fun createAndStart(
        playerColor: PlayerColor,
        playerServiceAccountId: String,
        botType: BotType,
        clockSettings: ClockSettings,
        coroutineScope: CoroutineScope,
    ): PlayerGameControls {
        val liveGame = LiveGameController(
            //TODO: add more clock options
            clockSettings = clockSettings,
            whitePlayerId = if(playerColor == PlayerColor.WHITE) playerServiceAccountId else botType.serviceAccountId,
            blackPlayerId = if(playerColor == PlayerColor.BLACK) playerServiceAccountId else botType.serviceAccountId,
            database = database,
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
