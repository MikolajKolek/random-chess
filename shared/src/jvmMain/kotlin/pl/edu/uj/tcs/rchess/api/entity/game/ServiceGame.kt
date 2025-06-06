package pl.edu.uj.tcs.rchess.api.entity.game

import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.model.PlayerColor

/**
 * Any game played online in a service (Random Chess or an external service)
 */
sealed interface ServiceGame: ApiGame {
    override val blackPlayer: ServiceAccount
    override val whitePlayer: ServiceAccount

    override fun getPlayer(color: PlayerColor): ServiceAccount =
        when (color) {
            PlayerColor.BLACK -> blackPlayer
            PlayerColor.WHITE -> whitePlayer
        }

    val userPlayedAs: PlayerColor?
        get() = PlayerColor.entries.singleOrNull { getPlayer(it).isCurrentUser }
}
