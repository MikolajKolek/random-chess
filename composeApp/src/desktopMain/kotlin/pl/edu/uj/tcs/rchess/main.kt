package pl.edu.uj.tcs.rchess

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import io.github.oshai.kotlinlogging.KotlinLogging
import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.config.Config
import pl.edu.uj.tcs.rchess.server.Server
import pl.edu.uj.tcs.rchess.view.RandomChessApp
import java.io.File

private val config: Config = ConfigLoaderBuilder.default().addFileSource(File("config.yml")).build().loadConfigOrThrow()
private val clientApi: ClientApi = Server(config)
val logger = KotlinLogging.logger("RandomChess")

fun main() {
    val app = RandomChessApp(clientApi)
    app.main()
}
