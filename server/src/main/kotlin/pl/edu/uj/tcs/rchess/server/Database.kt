package pl.edu.uj.tcs.rchess.server

import kotlinx.coroutines.CoroutineScope
import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryServiceGame
import pl.edu.uj.tcs.rchess.UnsavedServiceGame
import pl.edu.uj.tcs.rchess.model.state.GameState

/**
 * An interface used by the server to access the database.
 */
internal interface Database {
    val databaseScope: CoroutineScope

    /**
     * Stops the timer in the provided liveGameController, saves the
     * GameState to the database and completes [LiveGameController.finishedGame].
     */
    suspend fun saveGame(
        game: GameState,
        liveGameController: LiveGameController,
    )

    /**
     * Saves the given [UnsavedServiceGame]s to the database.
     */
    suspend fun saveServiceGames(games: List<UnsavedServiceGame>)

    /**
     * Returns the latest [pl.edu.uj.tcs.rchess.api.entity.game.ServiceGame]s linked to the
     * service account, or null if there are none.
     */
    suspend fun getLatestGameForServiceAccount(serviceAccount: ServiceAccount): HistoryServiceGame?

    /**
     * Gets an access token linked to a service account.
     */
    suspend fun getTokenForServiceAccount(serviceAccount: ServiceAccount): String?
}
