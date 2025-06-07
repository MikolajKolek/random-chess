package pl.edu.uj.tcs.rchess.api.args

import pl.edu.uj.tcs.rchess.api.entity.Service
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryGame

data class GamesRequestArgs(
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
     * The number of games that should be returned.
     *
     * If this is null, all matching games are returned.
     */
    val length: Int? = 100,
    /**
     * If true, this sets [pl.edu.uj.tcs.rchess.api.DatabaseState.updatesAvailable] to `false` in the
     * [pl.edu.uj.tcs.rchess.api.ClientApi.databaseState] flow after the request is made.
     */
    val clearUpdatesAvailable: Boolean = false
)
