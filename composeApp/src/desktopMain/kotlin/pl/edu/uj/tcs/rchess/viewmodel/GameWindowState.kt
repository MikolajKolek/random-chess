package pl.edu.uj.tcs.rchess.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import pl.edu.uj.tcs.rchess.api.entity.game.ApiGame
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryGame
import pl.edu.uj.tcs.rchess.api.entity.game.LiveGame
import pl.edu.uj.tcs.rchess.api.entity.game.ServiceGame
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.state.BoardState
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import pl.edu.uj.tcs.rchess.model.state.GameState

/**
 * Used as the state and business logic management mechanism for [GameScreen]
 */
interface GameWindowState {
    val game: ApiGame

    val gameState: GameState

    val orientation: PlayerColor

    fun flipOrientation()

    interface Resignation {
        val dialogVisible: Boolean
        fun openDialog()
        fun cancelResignation()
        fun confirmResignation()
    }

    /**
     * null if the user cannot resign (no input passed or the game is not running)
     */
    val resignation: Resignation?

    /**
     * Returns the color of the input.player if the currently displayed board
     * should allow the player to make a move.
     */
    val moveEnabledForColor: PlayerColor?

    /**
     * Returns true if the game is running and it's input.player's turn
     */
    val waitingForOwnMove: Boolean

    fun makeMove(move: Move)

    val boardStateBrowser: SyncedListBrowser<BoardState>
}

@Composable
fun rememberGameWindowState(game: ApiGame): GameWindowState {
    val coroutineScope = rememberCoroutineScope()

    val gameState by when (game) {
        is HistoryGame -> derivedStateOf { game.finalGameState }
        is LiveGame -> {
            game.controls.observer.stateFlow.collectAsStateWithLifecycle()
        }
    }
    val input = (game as? LiveGame)?.controls?.input

    val orientation = remember { mutableStateOf(
        input?.playerColor
                ?: (game as? ServiceGame)?.userPlayedAs
                ?: PlayerColor.WHITE
    ) }
    val makeMoveLoading = remember { mutableStateOf(false) }
    val boardStateBrowser = rememberListBrowser(gameState.boardStates)

    return object : GameWindowState {
        override val game = game

        override val gameState = gameState

        override val orientation by orientation

        override fun flipOrientation() {
            orientation.value = orientation.value.opponent
        }

        override val resignation =
            input.takeIf { gameState.progress is GameProgress.Running }?.let { resignInput ->
                object : GameWindowState.Resignation {
                    private var _dialogVisible by remember { mutableStateOf(false) }
                    override val dialogVisible = _dialogVisible

                    override fun openDialog() {
                         _dialogVisible = true
                    }

                    override fun cancelResignation() {
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

        override val moveEnabledForColor = input?.let { input ->
            input.playerColor.takeIf {
                !makeMoveLoading.value &&
                    gameState.progress is GameProgress.Running &&
                    boardStateBrowser.lastSelected &&
                    gameState.currentState.currentTurn == input.playerColor
            }
        }

        override val waitingForOwnMove =
            gameState.progress is GameProgress.Running &&
                gameState.currentState.currentTurn == input?.playerColor

        override fun makeMove(move: Move) {
            input?.takeIf { moveEnabledForColor != null }?.let {
                makeMoveLoading.value = true
                coroutineScope.launch {
                    // TODO: Handle errors, needed in case we introduce a client-server architecture
                    try {
                        it.makeMove(move)
                    } finally {
                        makeMoveLoading.value = false
                    }
                }
            }
        }

        override val boardStateBrowser = boardStateBrowser
    }
}
