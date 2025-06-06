package pl.edu.uj.tcs.rchess.api.entity.game

import pl.edu.uj.tcs.rchess.api.entity.PlayerDetails
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.PlayerColor

/**
 * Any game that can be returned from the API
 */
sealed interface ApiGame {
    val clockSettings: ClockSettings?
    val whitePlayer: PlayerDetails
    val blackPlayer: PlayerDetails

    fun getPlayer(color: PlayerColor): PlayerDetails
}
