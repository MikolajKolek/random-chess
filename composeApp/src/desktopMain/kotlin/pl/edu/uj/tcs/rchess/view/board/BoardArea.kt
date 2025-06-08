package pl.edu.uj.tcs.rchess.view.board

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.times
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.state.BoardState

@Composable
fun BoardArea(
    modifier: Modifier = Modifier,
    state: BoardState,
    orientation: PlayerColor,
    moveEnabledForColor: PlayerColor? = null,
    onMove: (Move) -> Unit = {},
    drawPlayerBar: @Composable (
        modifier: Modifier,
        color: PlayerColor,
    ) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .padding(32.dp)
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        val labelsPadding = 24.dp
        val pieceSize = min(
            minWidth - 2 * labelsPadding,
            minHeight - 2 * labelsPadding - 2 * PlayerBar.height
        ) / 8

        @Composable
        fun FileLabelRow() {
            Row(
                modifier = Modifier.padding(horizontal = labelsPadding).height(labelsPadding).fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                for (file in filesFor(orientation)) {
                    Box(
                        modifier = Modifier.width(pieceSize).fillMaxHeight()
                    ) {
                        Text(
                            (
                                    'a' + file).toString(),
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }
            }
        }

        @Composable
        fun RankLabelColumn() {
            Column(
                modifier = Modifier.width(labelsPadding).fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
            ) {
                for (rank in ranksFor(orientation)) {
                    Box(
                        modifier = Modifier.height(pieceSize).fillMaxWidth()
                    ) {
                        Text(
                            (
                                    1 + rank).toString(),
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }
            }
        }

        val boardSize = 8 * pieceSize

        Column(
            modifier = Modifier
                .width(boardSize + 2 * labelsPadding)
                .height(boardSize + 2 * labelsPadding + 2 * PlayerBar.height),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            drawPlayerBar(Modifier.width(boardSize), orientation.opponent)
            FileLabelRow()
            Row(
                modifier = Modifier.fillMaxWidth().height(boardSize),
            ) {
                RankLabelColumn()
                BoardView(
                    pieceSize = pieceSize,
                    state = state,
                    orientation = orientation,
                    moveEnabledForColor = moveEnabledForColor,
                    onMove = onMove,
                )
                RankLabelColumn()
            }
            FileLabelRow()
            drawPlayerBar(Modifier.width(boardSize), orientation)
        }
    }
}
