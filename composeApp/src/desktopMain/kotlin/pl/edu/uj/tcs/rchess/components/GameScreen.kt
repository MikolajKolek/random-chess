package pl.edu.uj.tcs.rchess.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.components.board.LabeledBoard
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.game.GameInput
import pl.edu.uj.tcs.rchess.model.state.GameState

@Composable
fun GameScreen(
    gameState: GameState,
    input: GameInput?,
) {
    val orientation = remember {
        mutableStateOf(input?.playerColor ?: PlayerColor.WHITE)
    }
    val boardStateIndex = remember { mutableStateOf(0) }

    if (boardStateIndex.value >= gameState.boardStates.size) {
        boardStateIndex.value = gameState.boardStates.size - 1
    }
    val boardState = gameState.boardStates[boardStateIndex.value]

    val isInitial = boardStateIndex.value == 0
    val isCurrent = boardStateIndex.value == gameState.boardStates.size - 1

    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                enabled = !isInitial,
                onClick = {
                    boardStateIndex.value--
                }
            ) {
                Text("Previous")
            }

            Button(
                enabled = !isCurrent,
                onClick = {
                    boardStateIndex.value++
                }
            ) {
                Text("Next")
            }

            Button(
                onClick = {
                    orientation.value = orientation.value.opponent
                }
            ) {
                Text("Rotate")
            }
        }

        LabeledBoard(
            state = boardState,
            orientation = orientation.value,
            moveEnabledForColor = input?.takeIf { isCurrent }?.playerColor,
            onMove = { move ->
                input?.makeMove(move)
            },
        )
    }
}
