package pl.edu.uj.tcs.rchess.api.entity.game

import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.model.PlayerColor

/**
 * Any game played online in a service (Random Chess or an external service)
 */
sealed interface ServiceGame: ApiGame {
    val blackPlayer: ServiceAccount
    val whitePlayer: ServiceAccount

    fun getPlayer(color: PlayerColor): ServiceAccount = when (color) {
        PlayerColor.WHITE -> whitePlayer
        PlayerColor.BLACK -> blackPlayer
    }

    val userPlayedAs: PlayerColor?
        get() = PlayerColor.entries.singleOrNull { getPlayer(it).isCurrentUser }

    override fun getPlayerName(playerColor: PlayerColor): String =
        getPlayer(playerColor).displayName
}
