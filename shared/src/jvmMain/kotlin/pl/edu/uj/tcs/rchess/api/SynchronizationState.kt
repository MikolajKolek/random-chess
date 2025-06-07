package pl.edu.uj.tcs.rchess.api

import pl.edu.uj.tcs.rchess.api.entity.Service

sealed interface SynchronizationState

/**
 * The database is currently synchronizing with external services
 */
class Synchronizing : SynchronizationState

/**
 * The database is not currently synchronizing with external services
 */
data class Synchronized(
    /**
     * Lists all services that encountered errors during the last synchronization.
     */
    val errors: List<Service>
) : SynchronizationState