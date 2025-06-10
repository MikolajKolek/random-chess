package pl.edu.uj.tcs.rchess.api.entity

import kotlinx.coroutines.Deferred

/**
 * A response from the [pl.edu.uj.tcs.rchess.api.ClientApi.addExternalAccount] method call.
 */
data class AddExternalAccountResponse(
    /**
     * A URL that the user needs to visit to begin the linking process.
     */
    val oAuthUrl: String,

    /**
     * A [Deferred] instance which completes (possibly exceptionally),
     * when the external account linking process is complete.
     */
    val completionCallback: Deferred<Unit>
)