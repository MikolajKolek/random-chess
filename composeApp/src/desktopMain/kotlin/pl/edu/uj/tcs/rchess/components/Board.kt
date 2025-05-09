package pl.edu.uj.tcs.rchess.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

@Composable
@Preview
fun Board(state: BoardState, playerColor: PlayerColor) {
    val rows = when (playerColor) {
        PlayerColor.WHITE -> 7 downTo 0
        PlayerColor.BLACK -> 0..7
    }
    val columns = when (playerColor) {
        PlayerColor.WHITE -> 0..7
        PlayerColor.BLACK -> 7 downTo 0
    }

    Column(
        modifier = Modifier.width(512.dp).height(512.dp).border(2.dp, Color.Black),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        for (row in rows) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                for (col in columns) {
                    val square = Square(row = row, col = col)
                    val piece = state.getPieceAt(square)

                    // TODO: Also replace with enum
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .let {
                                if (square.isDark) {
                                    it.background(Color.LightGray)
                                } else {
                                    it
                                }
                            }
                    ) {
                        Text("$row $col: ${piece?.fenLetter ?: "-"}")
                    }
                }
            }
        }
    }
}
