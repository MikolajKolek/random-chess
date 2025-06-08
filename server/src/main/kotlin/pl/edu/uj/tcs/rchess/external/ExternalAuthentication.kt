package pl.edu.uj.tcs.rchess.external

import pl.edu.uj.tcs.rchess.api.entity.AddExternalAccountResponse
import pl.edu.uj.tcs.rchess.server.Database

internal interface ExternalAuthentication {
    val database: Database
    val userId: Int

    /**
     * Starts the authentication flow with an external service.
     */
    suspend fun authenticate(): AddExternalAccountResponse
}