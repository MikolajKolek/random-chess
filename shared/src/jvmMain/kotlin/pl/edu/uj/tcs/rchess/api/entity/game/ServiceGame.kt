package pl.edu.uj.tcs.rchess.api.entity.game

import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.model.PlayerColor

/**
 * Any game played online in a service (Random Chess or an external service)
 */
sealed interface ServiceGame: ApiGame {
    val blackPlayer: ServiceAccount
    val whitePlayer: ServiceAccount

    override fun getPlayerName(playerColor: PlayerColor): String =
        when (playerColor) {
            PlayerColor.BLACK -> blackPlayer.displayName
            PlayerColor.WHITE -> whitePlayer.displayName
        }
}
