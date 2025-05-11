package pl.edu.uj.tcs.rchess.components.board

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor

@Composable
@Preview
fun LabeledBoard(
    state: BoardState,
    orientation: PlayerColor,
    moveEnabledForColor: PlayerColor? = null,
    onMove: (Move) -> Unit = {},
) {
    val labelsPadding = 24.dp
    val pieceSize = 64.dp

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
                    Text((
                            'a' + file).toString(),
                        modifier =  Modifier.align(Alignment.Center),
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
                    Text((
                            1 + rank).toString(),
                        modifier =  Modifier.align(Alignment.Center),
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .width(8 * pieceSize + 2 * labelsPadding)
            .height(8 * pieceSize + 2 * labelsPadding),
    ) {
        FileLabelRow()
        Row(
            modifier = Modifier.fillMaxWidth().height(8 * pieceSize),
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
    }
}
