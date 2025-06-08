package pl.edu.uj.tcs.rchess.view

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.view.game.GameWindow
import pl.edu.uj.tcs.rchess.viewmodel.AppContext
import pl.edu.uj.tcs.rchess.viewmodel.navigation.Route
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.window_icon
import java.awt.Dimension

class RandomChessApp(private val clientApi: ClientApi) {
    fun main() = application {
        val state = rememberWindowState(
            placement = WindowPlacement.Maximized,
            size = DpSize(900.dp, 800.dp)
        )
        val context = remember { AppContext(clientApi) }

        val icon = painterResource(Res.drawable.window_icon)
        Window(
            onCloseRequest = ::exitApplication,
            state = state,
            title = "Random Chess",
            icon = icon,
        ) {
            window.minimumSize = Dimension(900, 600)
            LaunchedEffect(window) {
                context.navigation.storeMainWindowReference(window)
            }

            MainWindowContent(context)
        }

        context.navigation.gameWindows.forEachIndexed { index, game ->
            GameWindow(
                game,
                icon = icon,
                onCloseRequest = {
                    context.navigation.closeGameWindow(index)
                },
                onFinish = { historyGame ->
                    context.navigation.replaceGameWindow(index, historyGame)
                    context.gameHistoryViewModel.paging.refresh()
                    context.rankingListViewModel.refreshAll()
                },
                onSelectRanking = {
                    context.navigation.navigateTo(Route.Ranking(it))
                }
            )
        }
    }
}
