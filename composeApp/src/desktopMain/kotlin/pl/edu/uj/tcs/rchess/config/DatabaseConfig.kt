package pl.edu.uj.tcs.rchess.config

/**
 * Configuration for the PostgreSQL database connection.
 */
data class DatabaseConfig(
    val host: String,
    val port: Int,
    val database: String,
    val user: String,
    val password: String
)
