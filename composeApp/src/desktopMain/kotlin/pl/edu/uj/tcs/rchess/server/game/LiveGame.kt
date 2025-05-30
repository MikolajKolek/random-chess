package pl.edu.uj.tcs.rchess.server.game

import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.game.PlayerGameControls
import pl.edu.uj.tcs.rchess.server.ServiceAccount

data class LiveGame(
//    override val blackPlayer: ServiceAccount,
//    override val whitePlayer: ServiceAccount,
    val controls: PlayerGameControls,
) : ServiceGame {
    override val whitePlayer: ServiceAccount
        get() = TODO("Not yet implemented")

    override val blackPlayer: ServiceAccount
        get() = TODO("Not yet implemented")

    override fun getPlayerName(playerColor: PlayerColor): String =
        when (playerColor) {
            PlayerColor.BLACK -> "Black player"
            PlayerColor.WHITE -> "White player"
        }
}
