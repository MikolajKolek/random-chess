package pl.edu.uj.tcs.rchess

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import pl.edu.uj.tcs.rchess.server.ClientApi
import pl.edu.uj.tcs.rchess.server.Server
import java.io.File

val config: Config = ConfigLoaderBuilder.default().addFileSource(File("config.yml")).build().loadConfigOrThrow()
val clientApi: ClientApi = Server(config.database)

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Random Chess",
    ) {
        App()
    }
}