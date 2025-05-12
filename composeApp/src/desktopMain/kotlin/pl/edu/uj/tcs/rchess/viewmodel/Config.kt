package pl.edu.uj.tcs.rchess.viewmodel

import pl.edu.uj.tcs.rchess.bot.Bot

data class Config(
    val defaultUser: Int,
    val database: DatabaseConfig,
    val bots: ArrayList<BotType>
)

data class DatabaseConfig(
    val host: String,
    val port: Int,
    val database: String,
    val user: String,
    val password: String
)

data class BotType(
    val description: String?,
    val executable: String,
    val options: Map<String, String>,
    val maxDepth: Int?,
    val moveTimeMs: Int?,
    val serviceAccountId: String,
    val elo: Int
) {
    fun spawnBot(): Bot {
        return Bot(ProcessBuilder(executable).start(), options, maxDepth, moveTimeMs)
    }
}
