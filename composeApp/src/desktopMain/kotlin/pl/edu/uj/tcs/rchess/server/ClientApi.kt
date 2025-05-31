package pl.edu.uj.tcs.rchess.server

import kotlinx.coroutines.flow.StateFlow
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.server.game.HistoryGame
import pl.edu.uj.tcs.rchess.server.game.HistoryServiceGame
import pl.edu.uj.tcs.rchess.server.game.LiveGame
import pl.edu.uj.tcs.rchess.server.game.PgnGame

interface ClientApi {
    /**
     * A [StateFlow] indicating the current state of database synchronization.
     */
    val databaseState: StateFlow<DatabaseState>

    /**
     * @return A list of all [pl.edu.uj.tcs.rchess.server.game.HistoryGame]s the user has access to
     * @param refreshAvailableUpdates If true, this sets [DatabaseState.updatesAvailable] to `false` in the
     * [databaseState] flow.
     */
    suspend fun getUserGames(refreshAvailableUpdates: Boolean = false): List<HistoryGame>

    /**
     * @param id The ID of the service game
     * @return A [pl.edu.uj.tcs.rchess.server.game.HistoryServiceGame] from the database with the given ID, if it exists and the user has access to it
     * @throws IllegalArgumentException when the game does not exist or the user doesn't have access to it
     */
    suspend fun getServiceGame(id: Int): HistoryServiceGame

    /**
     * @param id The ID of the PGN game
     * @return A [pl.edu.uj.tcs.rchess.server.game.PgnGame] from the database with the given ID, if it exists and the user has access to it
     * @throws IllegalArgumentException when the game does not exist or the user doesn't have access to it
     */
    suspend fun getPgnGame(id: Int): PgnGame

    /**
     * @param fullPgn The full PGN string
     * @return The IDs of the newly added games
     * @throws IllegalArgumentException when the [fullPgn] argument is not a valid PGN database
     */
    suspend fun addPgnGames(fullPgn: String): List<Int>

    /**
     * @return The system [ServiceAccount] of the user
     */
    suspend fun getSystemAccount(): ServiceAccount

    /**
     * @return A list of [BotOpponent]s the user can start a game with
     */
    suspend fun getBotOpponents(): List<BotOpponent>

    /**
     * @param playerColor The color the player wants to play as. If null, the server will pick a random color.
     * @param botOpponent The bot opponent to play against.
     * @param clockSettings The clock settings to use.
     * @return [pl.edu.uj.tcs.rchess.model.game.PlayerGameControls] for the newly started game.
     */
    suspend fun startGameWithBot(
        playerColor: PlayerColor?,
        botOpponent: BotOpponent,
        clockSettings: ClockSettings,
        isRanked: Boolean,
    ): LiveGame

    /**
     * Requests a resync of data from external services to the database.
     *
     * If this function is called a second time before the first request finishes processing, the second call is ignored.
     *
     * When any new data is added, it's indicated in the [databaseState] flow
     * by setting [DatabaseState.updatesAvailable] to `true`.
     */
    suspend fun requestResync()

    data class DatabaseState(
        /**
         * Indicates if there are unread database updates.
         *
         * A database update is considered unread when no [getUserGames] requests with
         * `refreshUpdates = true` have been made since the update was made.
         */
        val updatesAvailable: Boolean,
        /**
         * Indicates if data from external services is currently being synchronized.
         */
        val synchronizing: Boolean,
    )
}
