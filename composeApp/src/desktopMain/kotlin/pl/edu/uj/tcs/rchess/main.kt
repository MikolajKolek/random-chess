package pl.edu.uj.tcs.rchess

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
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

fun main() = application {
    val state = rememberWindowState(placement = WindowPlacement.Maximized)
    val context = remember { AppContext(config, clientApi) }

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
            title = "Random Chess",
        ) {
            GameWindowContent(game)
        }
    }

    // TODO: Should use context.navigation in the future
    Window(
        onCloseRequest = ::exitApplication,
        title = "Random Chess Live Game",
    ) {
        LiveGameWindowContent(context)
    }
}
