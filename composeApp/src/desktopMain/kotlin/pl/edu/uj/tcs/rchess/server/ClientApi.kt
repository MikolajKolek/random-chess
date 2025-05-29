package pl.edu.uj.tcs.rchess.server

import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.server.game.HistoryGame
import pl.edu.uj.tcs.rchess.server.game.HistoryServiceGame
import pl.edu.uj.tcs.rchess.server.game.LiveGame
import pl.edu.uj.tcs.rchess.server.game.PgnGame

interface ClientApi {
    /**
     * @return A list of all [pl.edu.uj.tcs.rchess.server.game.HistoryGame]s the user has access to
     */
    suspend fun getUserGames(): List<HistoryGame>

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
     * @param playerColor The color the player wants to play as. If null the server will pick a random color.
     * @return [pl.edu.uj.tcs.rchess.model.game.PlayerGameControls] for the newly started game.
     */
    // TODO: Add a parameter for the bot opponent
    suspend fun startGameWithBot(
        playerColor: PlayerColor?,
    ): LiveGame
}
