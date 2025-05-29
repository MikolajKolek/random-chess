package pl.edu.uj.tcs.rchess.server

import pl.edu.uj.tcs.rchess.model.state.GameState
import pl.edu.uj.tcs.rchess.server.game.HistoryServiceGame

/**
 * An interface used by the server to access the database.
 */
interface Database {
    /**
     * Saves the GameState to the database and returns the created [HistoryServiceGame].
     */
    suspend fun saveGame(
        game: GameState,
        blackPlayerId: String,
        whitePlayerId: String
    ): HistoryServiceGame
}