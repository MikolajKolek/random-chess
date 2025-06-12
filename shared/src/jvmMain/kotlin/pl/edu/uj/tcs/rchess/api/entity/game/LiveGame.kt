package pl.edu.uj.tcs.rchess.api.entity.game

import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.game.PlayerGameControls

/**
 * A game that currently being played live.
 */
data class LiveGame(
    override val blackPlayer: ServiceAccount,
    override val whitePlayer: ServiceAccount,
    override val clockSettings: ClockSettings,
    val controls: PlayerGameControls,
) : ServiceGame {
    override val userPlayedAs: PlayerColor
        get() = controls.input.playerColor
}
