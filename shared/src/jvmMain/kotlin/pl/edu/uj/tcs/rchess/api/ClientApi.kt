package pl.edu.uj.tcs.rchess.api

import kotlinx.coroutines.flow.StateFlow
import pl.edu.uj.tcs.rchess.api.entity.BotOpponent
import pl.edu.uj.tcs.rchess.api.entity.Ranking
import pl.edu.uj.tcs.rchess.api.entity.Service
import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryGame
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryServiceGame
import pl.edu.uj.tcs.rchess.api.entity.game.LiveGame
import pl.edu.uj.tcs.rchess.api.entity.game.PgnGame
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.PlayerColor

interface ClientApi {
    /**
     * A [kotlinx.coroutines.flow.StateFlow] indicating the current state of database synchronization.
     */
    val databaseState: StateFlow<DatabaseState>

    /**
     * @return A list of all [HistoryGame]s the user has access to
     * that match the provided settings, sorted descending by
     * [HistoryGame.creationDate].
     */
    suspend fun getUserGames(settings: GamesRequestSettings): List<HistoryGame>

    /**
     * @param id The ID of the service game
     * @return A [HistoryServiceGame] from the database with the given ID, if it exists and the user has access to it
     * @throws IllegalArgumentException when the game does not exist or the user doesn't have access to it
     */
    suspend fun getServiceGame(id: Int): HistoryServiceGame

    /**
     * @param id The ID of the PGN game
     * @return A [PgnGame] from the database with the given ID, if it exists and the user has access to it
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
     * @return A list of all [Ranking]s the user can view.
     */
    suspend fun getRankings(): List<Ranking>

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

    data class GamesRequestSettings(
        /**
         * Whether the request should return PGN games.
         */
        val includePgnGames: Boolean = true,
        /**
         * The set of services games played on which should be included in the results.
         *
         * If this is null, this indicates that all services should be included.
         * If this is an empty set, this indicates that no service games should be included.
         */
        val includedServices: Set<Service>? = null,
        /**
         * Return games that are after the given [HistoryGame] in the game list.
         *
         * If this is null, the request returns games starting from the top of the list.
         */
        val after: HistoryGame? = null,
        /**
         * The number of games that should be returned
         */
        val length: Int = 100,
        /**
         * If true, this sets [DatabaseState.updatesAvailable] to `false` in the
         * [databaseState] flow after the request is made.
         */
        val refreshAvailableUpdates: Boolean = false
    )
}
