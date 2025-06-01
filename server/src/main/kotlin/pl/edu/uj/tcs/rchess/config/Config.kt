package pl.edu.uj.tcs.rchess.config

/**
 * Data class for storing configuration settings.
 */
internal data class Config(
    val defaultUser: Int,
    val database: DatabaseConfig,
    val bots: ArrayList<BotType>
)

