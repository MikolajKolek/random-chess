package pl.edu.uj.tcs.rchess.view

import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.view.game.GameWindow
import pl.edu.uj.tcs.rchess.viewmodel.AppContext
import java.awt.Dimension

class RandomChessApp(private val clientApi: ClientApi) {
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
                },
            )
        }
    }

}
