package pl.edu.uj.tcs.rchess.proxy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.api.entity.BotOpponent
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.server.Server
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong
import kotlin.time.Duration.Companion.milliseconds

/**
 * A demo implementation of a network layer for [pl.edu.uj.tcs.rchess.api.ClientApi].
 * This class simulates a poor network connection with extra delays and occasional errors.
 */
class DemoRemoteProxy(
    private val server: Server,
    initiallyEnabled: Boolean,
) : ClientApi {
    var enabled by mutableStateOf(initiallyEnabled)

    private suspend fun <T> networked(action: suspend () -> T): T {
        if (enabled) {
            delay(Random.nextLong(150L..600L).milliseconds)
            if (Random.nextInt(0..<100) < 25) {
                throw Exception("Demo network failure")
            }
        }
        return action()
    }

    override val databaseState
        get() = server.databaseState

    override suspend fun getUserGames(settings: ClientApi.GamesRequestSettings) =
        networked { server.getUserGames(settings) }

    override suspend fun getServiceGame(id: Int)
        = networked { server.getServiceGame(id) }

    override suspend fun getPgnGame(id: Int)
        = networked { server.getPgnGame(id) }

    override suspend fun addPgnGames(fullPgn: String)
        = networked { server.addPgnGames(fullPgn) }

    override suspend fun getSystemAccount()
        = networked { server.getSystemAccount() }

    override suspend fun getBotOpponents()
        = networked { server.getBotOpponents() }

    override suspend fun startGameWithBot(
        playerColor: PlayerColor?,
        botOpponent: BotOpponent,
        clockSettings: ClockSettings,
        isRanked: Boolean,
    )
        = networked { server.startGameWithBot(playerColor, botOpponent, clockSettings, isRanked) }

    override suspend fun getRankingsList()
        = networked { server.getRankingsList() }

    override suspend fun getRankingPlacements(settings: ClientApi.RankingRequestSettings)
        = networked { server.getRankingPlacements(settings) }

    override suspend fun requestResync()
        = networked { server.requestResync() }
}
