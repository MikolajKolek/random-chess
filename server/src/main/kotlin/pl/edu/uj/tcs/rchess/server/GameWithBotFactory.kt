package pl.edu.uj.tcs.rchess.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.edu.uj.tcs.rchess.config.BotType
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.api.game.PlayerGameControls

/**
 * A class for creating a live game with a bot.
 *
 * Implements the factory pattern.
 */
internal class GameWithBotFactory(
    private val database: Database
) {
    fun createAndStart(
        playerColor: PlayerColor,
        playerServiceAccountId: String,
        botType: BotType,
        clockSettings: ClockSettings,
        isRanked: Boolean,
        coroutineScope: CoroutineScope,
    ): PlayerGameControls {
        val liveGame = LiveGameController(
            clockSettings = clockSettings,
            whitePlayerId = if(playerColor == PlayerColor.WHITE) playerServiceAccountId else botType.serviceAccountId,
            blackPlayerId = if(playerColor == PlayerColor.BLACK) playerServiceAccountId else botType.serviceAccountId,
            isRanked = isRanked,
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
