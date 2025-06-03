package pl.edu.uj.tcs.rchess.api.entity.game

import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.game.PlayerGameControls

data class LiveGame(
//    override val blackPlayer: ServiceAccount,
//    override val whitePlayer: ServiceAccount,
    override val clockSettings: ClockSettings,
    val controls: PlayerGameControls,
) : ServiceGame {
    override val whitePlayer: ServiceAccount
        get() = TODO("Not yet implemented")

    override val blackPlayer: ServiceAccount
        get() = TODO("Not yet implemented")

    override val userPlayedAs: PlayerColor
        get() = controls.input.playerColor

    // TODO: Remove when whitePlayer and blackPlayer are implemented
    //  there implementation from ServiceGame will then be used
    override fun getPlayerName(playerColor: PlayerColor): String =
        when (playerColor) {
            PlayerColor.BLACK -> "Black player"
            PlayerColor.WHITE -> "White player"
        }
}
