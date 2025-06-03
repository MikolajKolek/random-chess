package pl.edu.uj.tcs.rchess.api.entity

/**
 * A data class describing a bot users in our service can play games with
 *
 * @param name The name of the bot
 * @param description A description of the bot
 * @param elo The estimated ELO of the bot
 * @param serviceAccountId The [ServiceAccount.userIdInService] of the bot
 */
data class BotOpponent(
    val name: String,
    val description: String?,
    val elo: Int,
    val serviceAccountId: String
)
