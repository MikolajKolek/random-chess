package pl.edu.uj.tcs.rchess.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.game.GameInput
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import pl.edu.uj.tcs.rchess.model.state.GameState

/**
 * Used as the state and business logic management mechanism for [GameScreen]
 */
interface GameViewState {
    val orientation: PlayerColor

    fun flipOrientation()

    interface Resignation {
        val dialogVisible: Boolean
        fun openDialog()
        fun closeDialog()
        fun confirmResignation()
    }

    /**
     * null if the user cannot resign (no input passed or the game is not running)
     */
    val resignation: Resignation?
}

@Composable
fun rememberGameViewState(
    gameState: GameState,
    input: GameInput?,
): GameViewState {
    val coroutineScope = rememberCoroutineScope()

    val orientation = remember { mutableStateOf(input?.playerColor ?: PlayerColor.WHITE) }

    val resignation: GameViewState.Resignation? = input
        .takeIf { gameState.progress is GameProgress.Running }
        ?.let { resignInput ->
            object : GameViewState.Resignation {
                private var _dialogVisible by remember { mutableStateOf(false) }
                override val dialogVisible = _dialogVisible

                override fun openDialog() {
                    confirmResignation()
                    // TODO: Restore when the dialog is implemented
//                    _dialogVisible = true
                }

                override fun closeDialog() {
                    _dialogVisible = false
                }

                override fun confirmResignation() {
                    coroutineScope.launch {
                        // TODO: Handle errors, needed in case we introduce a client-server architecture
                        resignInput.resign()
                    }
                    _dialogVisible = false
                }
            }
        }

    return object : GameViewState {
        override val orientation by orientation

        override fun flipOrientation() {
            orientation.value = orientation.value.opponent
        }

        override val resignation = resignation
    }
}