package pl.edu.uj.tcs.rchess

import androidx.compose.runtime.remember
import androidx.compose.ui.window.*
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import io.github.oshai.kotlinlogging.KotlinLogging
import pl.edu.uj.tcs.rchess.config.Config
import pl.edu.uj.tcs.rchess.server.ClientApi
import pl.edu.uj.tcs.rchess.server.Server
import pl.edu.uj.tcs.rchess.view.GameWindowContent
import pl.edu.uj.tcs.rchess.view.LiveGameWindowContent
import pl.edu.uj.tcs.rchess.view.MainWindowContent
import pl.edu.uj.tcs.rchess.viewmodel.AppContext
import java.io.File

private val config: Config = ConfigLoaderBuilder.default().addFileSource(File("config.yml")).build().loadConfigOrThrow()
private val clientApi: ClientApi = Server(config)
val logger = KotlinLogging.logger("RandomChess")

fun main() = application {
    val state = rememberWindowState(placement = WindowPlacement.Maximized)
    val context = remember { AppContext(clientApi) }

    Window(
        onCloseRequest = ::exitApplication,
        state = state,
        title = "Random Chess",
    ) {
        MainWindowContent(context)
    }

    context.navigation.gameWindows.forEachIndexed { index, game ->
        Window(
            onCloseRequest = {
                context.navigation.closeGameWindow(index)
            },
            title = "Random Chess history game",
            state = WindowState(
                placement = WindowPlacement.Maximized,
            ),
        ) {
            GameWindowContent(game)
        }
    }

    // TODO: Should use context.navigation in the future
    Window(
        onCloseRequest = ::exitApplication,
        title = "Random Chess live game",
        state = rememberWindowState(
            placement = WindowPlacement.Maximized,
        ),
    ) {
        LiveGameWindowContent(context)
    }
}
