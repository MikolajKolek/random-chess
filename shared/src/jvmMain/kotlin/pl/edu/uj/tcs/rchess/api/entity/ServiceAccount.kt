package pl.edu.uj.tcs.rchess.api.entity

/**
 * Data class representing a service account.
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
    override val displayName: String,
    val isBot: Boolean,
    val isCurrentUser: Boolean
): PlayerDetails
