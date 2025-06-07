package pl.edu.uj.tcs.rchess.server

import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryServiceGame
import pl.edu.uj.tcs.rchess.model.state.GameState

/**
 * An interface used by the server to access the database.
 */
internal interface Database {
    /**
     * Stops the timer in the provided liveGameController, saves the
     * GameState to the database and completes [LiveGameController.finishedGame].
     */
    suspend fun saveGame(
        game: GameState,
        liveGameController: LiveGameController,
    )

    /**
     * Returns the latest [pl.edu.uj.tcs.rchess.api.entity.game.ServiceGame]s linked to the
     * service account, or null if there are none.
     */
    suspend fun getLatestGameForServiceAccount(serviceAccount: ServiceAccount): HistoryServiceGame?

    /**
     * Returns all the [pl.edu.uj.tcs.rchess.api.entity.game.ServiceGame]s linked to the
     * service account that happened on the given epoch second, or null if there are none.
     */
    suspend fun getGamesAtSecondForServiceAccount(
        serviceAccount: ServiceAccount,
        epochSecond: Int
    ): HistoryServiceGame?
}
