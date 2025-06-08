package pl.edu.uj.tcs.rchess.view.gamesidebar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.api.entity.game.ApiGame
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryGame
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryServiceGame
import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.state.BoardState
import pl.edu.uj.tcs.rchess.view.shared.ExportField
import pl.edu.uj.tcs.rchess.view.shared.OpeningInfo
import pl.edu.uj.tcs.rchess.view.shared.ScrollableColumn

@Composable
fun InfoTab(
    modifier: Modifier = Modifier,
    currentBoardState: BoardState,
    game: ApiGame,
    orientation: PlayerColor,
    fenPinned: Boolean,
    onFenPinnedChange: (Boolean) -> Unit,
    onSelectMove: (number: Int) -> Unit,
) {
    ScrollableColumn(
        modifier = Modifier.fillMaxWidth().then(modifier),
        leftPadding = false,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            (game as? HistoryServiceGame)?.rankingUpdates?.takeIf { it.isNotEmpty() }?.let {
                Field("Ranking changes") {
                    RankingChanges(it)
                }
            }

            (game as? HistoryGame)?.opening?.let {
                Field("Opening") {
                    OpeningInfo(
                        modifier = Modifier.fillMaxWidth(),
                        opening = it,
                        orientation = orientation,
                        onClick = { onSelectMove(it.moveNumber) },
                    )
                }
            }

            (game as? HistoryGame)?.let { historyGame ->
                Field("PGN") {
                    ExportField(
                        value = historyGame.pgnString,
                        downloadEnabled = true,
                    )
                }
            }

            if (!fenPinned) {
                Field("FEN after current move", false, onFenPinnedChange) {
                    ExportField(
                        value = currentBoardState.toFenString(),
                        downloadEnabled = false,
                    )
                }
            }
        }
    }
}
