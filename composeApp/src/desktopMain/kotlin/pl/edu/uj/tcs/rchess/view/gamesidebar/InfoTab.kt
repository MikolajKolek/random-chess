package pl.edu.uj.tcs.rchess.view.gamesidebar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.state.BoardState
import pl.edu.uj.tcs.rchess.server.game.ApiGame
import pl.edu.uj.tcs.rchess.server.game.HistoryGame
import pl.edu.uj.tcs.rchess.view.shared.ExportField
import pl.edu.uj.tcs.rchess.view.shared.OpeningInfo

@Composable
fun InfoTab(
    modifier: Modifier = Modifier,
    currentBoardState: BoardState,
    game: ApiGame,
    orientation: PlayerColor,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .then(modifier),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            @Composable
            fun Field(label: String, content: @Composable () -> Unit) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(label, style = MaterialTheme.typography.labelLarge)
                    content()
                }
            }

            (game as? HistoryGame)?.opening?.let {
                Field("Opening") {
                    OpeningInfo(
                        modifier = Modifier.fillMaxWidth(),
                        opening = it,
                        orientation = orientation,
                    )
                }
            }

            Field("FEN after current move") {
                ExportField(
                    value = currentBoardState.toFenString(),
                    downloadEnabled = false,
                )
            }

            (game as? HistoryGame)?.let { historyGame ->
                Field("PGN") {
                    ExportField(
                        value = historyGame.pgnString,
                        downloadEnabled = true,
                    )
                }
            }
        }
    }
}
