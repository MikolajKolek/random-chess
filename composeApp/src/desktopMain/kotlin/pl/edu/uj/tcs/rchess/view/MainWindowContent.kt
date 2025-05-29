package pl.edu.uj.tcs.rchess.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.edu.uj.tcs.rchess.view.newgame.NewGameDialog
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
            is Route.RankingList -> PlaceholderScreen(text = "Ranking list")
            is Route.TournamentList -> PlaceholderScreen(text = "Tournament list")
            is Route.Account -> PlaceholderScreen(text = "Account")
            is Route.Ranking -> PlaceholderScreen(text = "Ranking ${route.rankingId}")
            is Route.Tournament -> PlaceholderScreen(text = "Tournament ${route.tournamentId}")
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
            )

            Box(
                modifier = Modifier.fillMaxSize().widthIn(max = 600.dp),
            ) {
                RouteScreen(context)
            }
        }
    }
}
