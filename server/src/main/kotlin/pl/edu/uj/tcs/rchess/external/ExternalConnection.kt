package pl.edu.uj.tcs.rchess.external

import pl.edu.uj.tcs.rchess.api.entity.Service
import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.external.lichess.LichessConnection
import pl.edu.uj.tcs.rchess.server.Database

/**
 * An object responsible for maintaining a connection with an external
 * game service for synchronization.
 */
internal interface ExternalConnection {
    val database: Database
    val serviceAccount: ServiceAccount

    /**
     * @return Whether the external service is ready for synchronization.
     * This can be false if, for example, another request was recently made,
     * and we don't want to hit the rate limit.
     */
    fun available(): Boolean

    /**
     * Synchronizes the current state of the external service to the database,
     * if the service is ready for synchronization ([available] is true).
     *
     * This method is NOT thread-safe - it must not be called from multiple threads or coroutine scopes.
     *
     * This method never throws. All errors are caught inside and logged.
     * In case of an error, this method returns `false`.
     *
     * @return True if the synchronization was successful, false if it wasn't.
     */
    suspend fun synchronize(): Boolean

    companion object {
        fun fromServiceAccount(account: ServiceAccount, database: Database): ExternalConnection?
        = when(account.service) {
            Service.LICHESS -> LichessConnection(database, account)
            else -> null
        }
    }
}