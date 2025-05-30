package pl.edu.uj.tcs.rchess.view.gamesidebar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString
import pl.edu.uj.tcs.rchess.model.state.BoardState
import pl.edu.uj.tcs.rchess.server.game.ApiGame
import pl.edu.uj.tcs.rchess.server.game.HistoryGame
import pl.edu.uj.tcs.rchess.view.shared.ExportField

@Composable
fun InfoTab(
    modifier: Modifier = Modifier,
    currentBoardState: BoardState,
    game: ApiGame,
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
            .then(modifier),
    ) {
        ExportField(
            label = "FEN after current move",
            value = currentBoardState.toFenString(),
        )

        (game as? HistoryGame)?.let { historyGame ->
            ExportField(
                label = "PGN",
                value = historyGame.pgnString
            )
        }
    }
}
