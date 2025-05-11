package pl.edu.uj.tcs.rchess.components.board

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

/**
 * Get the rank indexes as they appear from top to bottom on the screen
 * when playing as the player with color [orientation].
 */
fun ranksFor(orientation: PlayerColor) = when (orientation) {
    PlayerColor.WHITE -> 7 downTo 0
    PlayerColor.BLACK -> 0..7
}

/**
 * Get the file indexes as they appear from left to right on the screen
 * when playing as the player with color [orientation].
 */
fun filesFor(orientation: PlayerColor) = when (orientation) {
    PlayerColor.WHITE -> 0..7
    PlayerColor.BLACK -> 7 downTo 0
}

@Composable
fun BoardView(
    pieceSize: Dp,
    state: BoardState,
    orientation: PlayerColor,
    moveEnabledForColor: PlayerColor? = null,
) {
    val moveAvailableForColor = moveEnabledForColor?.takeIf {
        it == state.currentTurn
    }

    Column(
        modifier = Modifier
            .width(8 * pieceSize)
            .height(8 * pieceSize),
    ) {
        for (rank in ranksFor(orientation)) {
            Row(
                Modifier.fillMaxWidth().weight(1f),
            ) {
                for (file in filesFor(orientation)) {
                    val square = Square(rank = rank, file = file)
                    val piece = state.board[square]

                    Box(
                        modifier = Modifier
                            .weight(1f)
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
                            },
                    ) {
                        if (piece != null) {
                            Text(
                                piece.unicodeSymbol,
                                fontSize = 48.sp,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    }
                }
            }
        }
    }
}
