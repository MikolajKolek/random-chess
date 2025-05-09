package pl.edu.uj.tcs.rchess.server

/**
 * Data class representing a service account. These objects are only created in response to a client's request
 * and are not used internally by the server for any other uses.
 *
 * @param service The service the account belongs to.
 * @param userIdInService The user ID of the account in the service.
 * @param displayName The display name of the account.
 * @param isBot Whether the account is a bot or not.
 * @param isCurrentUser Whether the account is associated with the currently logged-in user.
 */
data class ServiceAccount(
    val service: Service,
    val userIdInService: String,
    val displayName: String,
    val isBot: Boolean,
    val isCurrentUser: Boolean
)