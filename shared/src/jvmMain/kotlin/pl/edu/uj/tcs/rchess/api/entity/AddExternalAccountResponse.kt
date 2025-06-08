package pl.edu.uj.tcs.rchess.api.entity

import kotlinx.coroutines.Deferred

/**
 * A response from the [pl.edu.uj.tcs.rchess.api.ClientApi.addExternalAccount] method call
 */
data class AddExternalAccountResponse(
    /**
     * An URL that the user needs to visit to complete the linking process
     */
    val oAuthUrl: String,

    /**
     * A [Deferred] instance which completes (possibly exceptionally),
     * when the linking process is done.
     */
    val completionCallback: Deferred<Unit>
)