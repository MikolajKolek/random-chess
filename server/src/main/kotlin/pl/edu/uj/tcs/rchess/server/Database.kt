package pl.edu.uj.tcs.rchess.server

import pl.edu.uj.tcs.rchess.api.entity.game.HistoryServiceGame
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.state.GameState

/**
 * An interface used by the server to access the database.
 */
internal interface Database {
    /**
     * Saves the GameState to the database and returns the created [HistoryServiceGame].
     */
    suspend fun saveGame(
        game: GameState,
        blackPlayerId: String,
        whitePlayerId: String,
        isRanked: Boolean,
        clockSettings: ClockSettings,
    ): HistoryServiceGame
}
