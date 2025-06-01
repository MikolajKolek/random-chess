package pl.edu.uj.tcs.rchess.config

import pl.edu.uj.tcs.rchess.bot.Bot

/**
 * Config for describing a bot.
 *
 * It has the information required to spawn [pl.edu.uj.tcs.rchess.bot.Bot] instances.
 */
internal data class BotType(
    val description: String?,
    val executable: String,
    val options: Map<String, String>,
    val maxDepth: Int?,
    val moveTimeMs: Int?,
    val serviceAccountId: String,
    val elo: Int,
    val slowdown: Pair<Int, Int>?
) {
    fun spawnBot(): Bot {
        return Bot(ProcessBuilder(executable).start(), options, maxDepth, moveTimeMs, slowdown)
    }
}
