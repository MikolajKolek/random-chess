package pl.edu.uj.tcs.rchess.server

interface ClientApi {
    /**
     * @return A list of all [HistoryGame]s the user has access to
     *
     * TODO: ewentualnie można to zmodyfikować: w sytuacjach w których chcemy mieć listę HistoryGame, wystarczyłoby nam
     *  może samo entry z VIEW "games" poszerzone o display names obu stron?
     */
    suspend fun getUserGames(): List<HistoryGame>

    /**
     * @param id The ID of the service game
     * @return A [ServiceGame] from the database with the given ID, if it exists and the user has access to it
     * @throws IllegalArgumentException when the game does not exist or the user doesn't have access to it
     */
    suspend fun getServiceGame(id: Int): ServiceGame

    /**
     * @param id The ID of the PGN game
     * @return A [PgnGame] from the database with the given ID, if it exists and the user has access to it
     * @throws IllegalArgumentException when the game does not exist or the user doesn't have access to it
     */
    suspend fun getPgnGame(id: Int): PgnGame

    /**
     * @param fullPGN The full PGN string of the game
     * @return The ID of the newly added game
     * @throws IllegalArgumentException when the [fullPGN] argument is not a valid PGN
     */
    suspend fun addPGNGame(fullPGN: String): Int

    /**
     * @return The system [ServiceAccount] of the user
     */
    suspend fun getSystemAccount(): ServiceAccount
}