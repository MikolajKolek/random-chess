package pl.edu.uj.tcs.rchess

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import pl.edu.uj.tcs.rchess.config.Config
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.server.ClientApi
import pl.edu.uj.tcs.rchess.server.Server
import pl.edu.uj.tcs.rchess.view.MainWindowContent
import pl.edu.uj.tcs.rchess.view.game.GameWindow
import pl.edu.uj.tcs.rchess.viewmodel.AppContext
import java.awt.Dimension
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
        window.minimumSize = Dimension(800, 600)
        MainWindowContent(context)
    }

    context.navigation.gameWindows.forEachIndexed { index, game ->
        GameWindow(
            game,
            onCloseRequest = {
                context.navigation.closeGameWindow(index)
            }
        )
    }

    // TODO: Remove, this is just for testing before the new game dialog works
    LaunchedEffect(Unit) {
        runBlocking {
            context.navigation.openGameWindow(context.clientApi.startGameWithBot(PlayerColor.WHITE))
            context.navigation.openGameWindow(context.clientApi.startGameWithBot(PlayerColor.BLACK))
        }
    }
}
