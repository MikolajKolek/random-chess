package pl.edu.uj.tcs.rchess.view.board

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.times
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.edu.uj.tcs.rchess.model.state.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.state.ClockState

@Composable
@Preview
fun BoardArea(
    modifier: Modifier = Modifier,
    state: BoardState,
    orientation: PlayerColor,
    moveEnabledForColor: PlayerColor? = null,
    onMove: (Move) -> Unit = {},
    whiteClock: ClockState?,
    blackClock: ClockState?,
) {
    BoxWithConstraints(
        modifier = modifier.padding(32.dp),
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

        @Composable
        fun ScopedPlayerBar(playerColor: PlayerColor) {
            PlayerBar(
                modifier = Modifier.width(boardSize),
                color = playerColor,
                name = null,
                isSelf = playerColor == moveEnabledForColor,
                clockState = when (playerColor) {
                    PlayerColor.WHITE -> whiteClock
                    PlayerColor.BLACK -> blackClock
                },
            )
        }

        Column(
            modifier = Modifier
                .width(boardSize + 2 * labelsPadding)
                .height(boardSize + 2 * labelsPadding + 2 * PlayerBar.height),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ScopedPlayerBar(orientation.opponent)
            FileLabelRow()
            Row(
                modifier = Modifier.fillMaxWidth().height(boardSize),
            ) {
                RankLabelColumn()
                OutlinedCard {
                    BoardView(
                        pieceSize = pieceSize,
                        state = state,
                        orientation = orientation,
                        moveEnabledForColor = moveEnabledForColor,
                        onMove = onMove,
                    )
                }
                RankLabelColumn()
            }
            FileLabelRow()
            ScopedPlayerBar(orientation)
        }
    }
}
