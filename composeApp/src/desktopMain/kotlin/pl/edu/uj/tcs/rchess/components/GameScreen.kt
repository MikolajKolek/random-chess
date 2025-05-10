package pl.edu.uj.tcs.rchess.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import pl.edu.uj.tcs.rchess.model.GameInput
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.state.ImmutableGameState

@Composable
fun GameScreen(
    gameState: ImmutableGameState,
    input: GameInput?,
) {
    val playerColor = remember {
        mutableStateOf(input?.getColor() ?: PlayerColor.WHITE)
    }
    val boardStateIndex = remember { mutableStateOf(0) }

    if (boardStateIndex.value >= gameState.boardStates.size) {
        boardStateIndex.value = gameState.boardStates.size - 1
    }
    val boardState = gameState.boardStates[boardStateIndex.value]

    val isInitial = boardStateIndex.value == 0
    val isCurrent = boardStateIndex.value == gameState.boardStates.size - 1

    Column {
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

        Board(
            state = boardState,
            orientation = playerColor.value,
            // TODO: Remove WHITE default, it's only for testing
            moveEnabledForColor = input?.takeIf { isCurrent }?.getColor() ?: PlayerColor.WHITE,
        )
    }
}
