package pl.edu.uj.tcs.rchess.api

import kotlinx.coroutines.flow.StateFlow
import pl.edu.uj.tcs.rchess.api.args.GamesRequestArgs
import pl.edu.uj.tcs.rchess.api.args.RankingRequestArgs
import pl.edu.uj.tcs.rchess.api.entity.AddExternalAccountResponse
import pl.edu.uj.tcs.rchess.api.entity.BotOpponent
import pl.edu.uj.tcs.rchess.api.entity.Service
import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryGame
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryServiceGame
import pl.edu.uj.tcs.rchess.api.entity.game.LiveGame
import pl.edu.uj.tcs.rchess.api.entity.game.PgnGame
import pl.edu.uj.tcs.rchess.api.entity.ranking.Ranking
import pl.edu.uj.tcs.rchess.api.entity.ranking.RankingSpot
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.PlayerColor

/**
 * The API used for all communication between the client and server modules.
 */
interface ClientApi {
    /**
     * A [StateFlow] indicating the current state of database synchronization.
     */
    val databaseState: StateFlow<DatabaseState>

    /**
     * A [StateFlow] listing all service accounts linked to the current user.
     *
     * If a new account is successfully linked, this flow is immediately updated.
     */
    val serviceAccounts: StateFlow<Set<ServiceAccount>>

    /**
     * @return A list of all [HistoryGame]s the user has access to
     * that match the provided settings, sorted descending by
     * [HistoryGame.creationDate].
     */
    suspend fun getUserGames(settings: GamesRequestArgs): List<HistoryGame>

    /**
     * @param id The ID of the service game.
     * @return A [HistoryServiceGame] from the database with the given ID, if it exists and the user has access to it.
     * @throws IllegalArgumentException when the game does not exist or the user doesn't have access to it.
     */
    suspend fun getServiceGame(id: Int): HistoryServiceGame

    /**
     * @param id The ID of the PGN game.
     * @return A [PgnGame] from the database with the given ID, if it exists and the user has access to it.
     * @throws IllegalArgumentException when the game does not exist or the user doesn't have access to it.
     */
    suspend fun getPgnGame(id: Int): PgnGame

    /**
     * @param fullPgn The full PGN string.
     * @return The IDs of the newly added games.
     * @throws IllegalArgumentException when the [fullPgn] argument is not a valid PGN database.
     */
    suspend fun addPgnGames(fullPgn: String): List<Int>

    /**
     * @return The system [ServiceAccount] of the user.
     */
    suspend fun getSystemAccount(): ServiceAccount

    /**
     * @return A list of [BotOpponent]s the user can start a game with.
     */
    suspend fun getBotOpponents(): List<BotOpponent>

    /**
     * @param playerColor The color the player wants to play as. If null, the server will pick a random color.
     * @param botOpponent The bot opponent to play against.
     * @param clockSettings The clock settings to use.
     * @return [pl.edu.uj.tcs.rchess.api.game.PlayerGameControls] for the newly started game.
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
    suspend fun getRankingsList(): List<Ranking>

    /**
     * @return The placements on a given ranking, ordered first-to-last.
     */
    suspend fun getRankingPlacements(settings: RankingRequestArgs): List<RankingSpot>

    /**
     * Requests a resync of data from external services to the database.
     *
     * If this function is called a second time before the first request
     * finishes processing, the second call is ignored.
     *
     * If the call caused synchronization to begin, changes
     * [DatabaseState.synchronizationState] to [Synchronizing].
     *
     * When any new data is added, it's indicated in the [databaseState] flow
     * by setting [DatabaseState.updatesAvailable] to `true`.
     */
    suspend fun requestResync()

    /**
     * Starts an authentication flow with an external service
     *
     * @throws IllegalArgumentException if called with an unsupported service, for example
     * [Service.UNKNOWN] or [Service.RANDOM_CHESS].
     */
    suspend fun addExternalAccount(service: Service): AddExternalAccountResponse
}
