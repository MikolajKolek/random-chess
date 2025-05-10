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

    val canGoToPrev = boardStateIndex.value > 0
    val canGoToNext = boardStateIndex.value < gameState.boardStates.size - 1

    Column {
        Button(
            enabled = canGoToPrev,
            onClick = {
                boardStateIndex.value--
            }
        ) {
            Text("Previous")
        }

        Button(
            enabled = canGoToNext,
            onClick = {
                boardStateIndex.value++
            }
        ) {
            Text("Next")
        }

        Board(
            state = boardState,
            playerColor = playerColor.value,
        )
    }
}
