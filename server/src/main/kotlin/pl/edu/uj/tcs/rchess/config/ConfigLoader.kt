package pl.edu.uj.tcs.rchess.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addPathSource
import java.nio.file.Paths
import kotlin.io.path.createDirectories

internal object ConfigLoader {
    private val configDirectory = run {
        val os = System.getProperty("os.name").lowercase()
        val userHome = System.getProperty("user.home")

        when {
            os.contains("win") -> {
                val appData = System.getenv("APPDATA") ?: "$userHome\\AppData\\Roaming"
                Paths.get(appData, "rchess")
            }
            os.contains("mac") -> {
                Paths.get(userHome, "Library", "Application Support", "rchess")
            }
            else -> {
                val xdgConfig = System.getenv("XDG_DATA_HOME") ?: "$userHome/.local/share"
                Paths.get(xdgConfig, "rchess")
            }
        }
    }

    private val configFile = configDirectory.resolve("config.yml")


    fun loadConfig(): Config {
        configDirectory.createDirectories()
        return ConfigLoaderBuilder
            .default()
            .addDecoder(ExecutableName.Decoder(configDirectory))
            .addPathSource(configFile)
            .build()
            .loadConfigOrThrow()
    }
}
