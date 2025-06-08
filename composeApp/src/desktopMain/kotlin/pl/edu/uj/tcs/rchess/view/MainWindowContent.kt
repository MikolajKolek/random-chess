package pl.edu.uj.tcs.rchess.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.edu.uj.tcs.rchess.proxy.DemoRemoteProxy
import pl.edu.uj.tcs.rchess.view.history.GameHistoryScreen
import pl.edu.uj.tcs.rchess.view.newgame.NewGameDialog
import pl.edu.uj.tcs.rchess.view.rankings.RankingsScreen
import pl.edu.uj.tcs.rchess.view.shared.PlaceholderScreen
import pl.edu.uj.tcs.rchess.view.theme.RandomChessTheme
import pl.edu.uj.tcs.rchess.viewmodel.AppContext
import pl.edu.uj.tcs.rchess.viewmodel.navigation.Route

@Composable
fun RouteScreen(
    context: AppContext,
) {
    context.navigation.route.let { route ->
        when (route) {
            is Route.GameHistory -> GameHistoryScreen(context)
            is Route.Rankings -> RankingsScreen(context)
            is Route.Ranking -> RankingsScreen(context, route.rankingId)
            is Route.TournamentList -> PlaceholderScreen(text = "Tournament list")
            is Route.Tournament -> PlaceholderScreen(text = "Tournament ${route.tournamentId}")
            is Route.Account -> PlaceholderScreen(text = "Account")
        }
    }
}

@Composable
@Preview
fun MainWindowContent(
    context: AppContext,
) {
    RandomChessTheme {
        if (context.navigation.newGameDialogVisible) {
            NewGameDialog(
                context,
                onClose = context.navigation::closeNewGameDialog,
            )
        }

        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
            Sidebar(
                context.navigation.route,
                context.navigation::navigateTo,
                onOpenNewGameDialog = context.navigation::openNewGameDialog,
                demoProxyEnabled = (context.clientApi as? DemoRemoteProxy)?.enabled,
                onSetDemoProxyEnabled = { value ->
                    (context.clientApi as? DemoRemoteProxy)?.let { proxy ->
                        proxy.enabled = value
                    }
                },
            )

            Box(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier.widthIn(max = 1000.dp).fillMaxSize(),
                ) {
                    RouteScreen(context)
                }
            }
        }
    }
}
