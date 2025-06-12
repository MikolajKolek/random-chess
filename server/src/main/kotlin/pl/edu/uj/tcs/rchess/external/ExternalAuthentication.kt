package pl.edu.uj.tcs.rchess.external

import pl.edu.uj.tcs.rchess.api.entity.AddExternalAccountResponse
import pl.edu.uj.tcs.rchess.api.entity.Service
import pl.edu.uj.tcs.rchess.external.lichess.LichessAuthentication
import pl.edu.uj.tcs.rchess.server.Database

internal interface ExternalAuthentication {
    val database: Database
    val userId: Int

    /**
     * Starts the authentication flow with an external service.
     */
    suspend fun authenticate(): AddExternalAccountResponse

    companion object {
        /**
         * An [ExternalAuthentication] instance for the given service.
         * @throws IllegalArgumentException if called for a service that
         * does not support authentication, for example [Service.RANDOM_CHESS].
         */
        fun fromService(service: Service, userId: Int, database: Database): ExternalAuthentication
        = when(service) {
            Service.LICHESS -> LichessAuthentication(database, userId)
            else -> throw IllegalArgumentException(
                "Adding external account on service $service is not supported"
            )
        }
    }
}