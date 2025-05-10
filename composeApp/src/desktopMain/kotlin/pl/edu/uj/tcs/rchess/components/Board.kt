package pl.edu.uj.tcs.rchess.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

@Composable
@Preview
fun Board(
    state: BoardState,
    orientation: PlayerColor,
    moveEnabledForColor: PlayerColor? = null,
) {
    val moveAvailableForColor = moveEnabledForColor?.takeIf {
        it == state.currentTurn
    }

    val ranks = when (orientation) {
        PlayerColor.WHITE -> 7 downTo 0
        PlayerColor.BLACK -> 0..7
    }
    val files = when (orientation) {
        PlayerColor.WHITE -> 0..7
        PlayerColor.BLACK -> 7 downTo 0
    }

    val labelsPadding = 32.dp
    val pieceSize = 64.dp

    @Composable
    fun FileLabelRow() {
        Row(
            modifier = Modifier.padding(horizontal = labelsPadding).height(labelsPadding).width(8 * pieceSize + 2 * labelsPadding),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            for (file in files) {
                Text(file.toString())
            }
        }
    }

    Box {
        Column(
            modifier = Modifier.width(8 * pieceSize + 2 * labelsPadding).height(8 * pieceSize + 2 * labelsPadding),
        ) {
            FileLabelRow()

            for (rank in ranks) {
                Row(
                    Modifier.fillMaxWidth().height(pieceSize),
                ) {
                    Box(
                        modifier = Modifier
                            .width(labelsPadding)
                            .fillMaxHeight()
                    ) {
                        Text(rank.toString())
                    }

                    for (file in files) {
                        val square = Square(rank = rank, file = file)
                        val piece = state.getPieceAt(square)

                        Box(
                            modifier = Modifier
                                .width(pieceSize)
                                .fillMaxHeight()
                                .let {
                                    if (square.isDark) {
                                        it.background(Color.LightGray)
                                    } else {
                                        it
                                    }
                                }
                                .let {
                                    if (moveAvailableForColor != null) {
                                        it.clickable(onClick = {})
                                    } else {
                                        it
                                    }
                                }
                        ) {
                            Text("$rank $file: ${piece?.fenLetter ?: "-"}")
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(labelsPadding)
                            .fillMaxHeight()
                    ) {
                        Text(rank.toString())
                    }
                }
            }

            FileLabelRow()
        }
    }
}
