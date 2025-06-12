package pl.edu.uj.tcs.rchess.proxy

import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.api.args.GamesRequestArgs
import pl.edu.uj.tcs.rchess.api.args.RankingRequestArgs
import pl.edu.uj.tcs.rchess.api.entity.BotOpponent
import pl.edu.uj.tcs.rchess.api.entity.Service
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.PlayerColor

/**
 * A class that proxies all [ClientApi] method calls through the [proxy] method.
 */
abstract class AbstractProxy(
    protected val server: ClientApi,
) : ClientApi {
    protected abstract suspend fun <T> proxy(action: suspend () -> T): T

    override val databaseState
        get() = server.databaseState

    override val serviceAccounts
        get() = server.serviceAccounts

    override suspend fun getUserGames(settings: GamesRequestArgs) =
        proxy { server.getUserGames(settings) }

    override suspend fun getServiceGame(id: Int)
            = proxy { server.getServiceGame(id) }

    override suspend fun getPgnGame(id: Int)
            = proxy { server.getPgnGame(id) }

    override suspend fun addPgnGames(fullPgn: String)
            = proxy { server.addPgnGames(fullPgn) }

    override suspend fun getSystemAccount()
            = proxy { server.getSystemAccount() }

    override suspend fun getBotOpponents()
            = proxy { server.getBotOpponents() }

    override suspend fun startGameWithBot(
        playerColor: PlayerColor?,
        botOpponent: BotOpponent,
        clockSettings: ClockSettings,
        isRanked: Boolean,
    ) = proxy { server.startGameWithBot(playerColor, botOpponent, clockSettings, isRanked) }

    override suspend fun getRankingsList()
            = proxy { server.getRankingsList() }

    override suspend fun getRankingPlacements(settings: RankingRequestArgs)
            = proxy { server.getRankingPlacements(settings) }

    override suspend fun requestResync()
            = proxy { server.requestResync() }

    override suspend fun addExternalAccount(service: Service)
            = proxy { server.addExternalAccount(service) }
}