package pl.edu.uj.tcs.rchess.server.game

import pl.edu.uj.tcs.rchess.model.PlayerColor

/**
 * Any game that can be returned from the API
 */
sealed interface ApiGame {
    fun getPlayerName(playerColor: PlayerColor): String
}
