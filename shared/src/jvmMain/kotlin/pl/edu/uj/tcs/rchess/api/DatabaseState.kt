package pl.edu.uj.tcs.rchess.api

data class DatabaseState(
    /**
     * Indicates if there are unread database updates.
     *
     * A database update is considered unread when no [ClientApi.getUserGames] requests with
     * `refreshUpdates = true` have been made since the update was made.
     */
    val updatesAvailable: Boolean,
    /**
     * Indicates if data from external services is currently being synchronized.
     */
    val synchronizationState: SynchronizationState,
)