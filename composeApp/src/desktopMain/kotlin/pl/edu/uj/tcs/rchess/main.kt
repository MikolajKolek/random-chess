package pl.edu.uj.tcs.rchess

import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import io.github.oshai.kotlinlogging.KotlinLogging
import pl.edu.uj.tcs.rchess.config.Config
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
    val state = rememberWindowState(
        placement = WindowPlacement.Maximized,
        size = DpSize(900.dp, 800.dp)
    )
    val context = remember { AppContext(clientApi) }

    Window(
        onCloseRequest = ::exitApplication,
        state = state,
        title = "Random Chess",
    ) {
        window.minimumSize = Dimension(700, 600)
        MainWindowContent(context)
    }

    context.navigation.gameWindows.forEachIndexed { index, game ->
        GameWindow(
            game,
            onCloseRequest = {
                context.navigation.closeGameWindow(index)
            },
            onFinish = { historyGame ->
                context.navigation.replaceGameWindow(index, historyGame)
                context.gameListViewModel.refresh()
            },
        )
    }
}
