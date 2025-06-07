package pl.edu.uj.tcs.rchess.external

import pl.edu.uj.tcs.rchess.api.entity.Service
import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
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
     * This method is guaranteed not to be called from multiple threads at the same time.
     * This method is guaranteed not to throw, always catching the error, logging it,
     * and returning false instead.
     * @return True if the synchronization was successful, false if it wasn't.
     */
    suspend fun synchronize(): Boolean
}

internal fun ServiceAccount.toExternalConnection(database: Database): ExternalConnection? = when(service) {
    Service.CHESS_COM -> ChessComConnection(database, this)
    else -> null
}