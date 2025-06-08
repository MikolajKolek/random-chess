package pl.edu.uj.tcs.rchess.viewmodel

import androidx.compose.runtime.*
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
     * Returns the color of the [input.player] if the currently displayed board
     * should allow the player to make a move.
     */
    val moveEnabledForColor: PlayerColor?

    /**
     * Returns true if the game is running, and it's [input.player]'s turn
     */
    val waitingForOwnMove: Boolean

    fun makeMove(move: Move)

    val boardStateBrowser: SyncedListBrowser<BoardState>

    var fenPinned: Boolean
}

@Composable
fun rememberGameWindowState(game: ApiGame): GameWindowState {
    val coroutineScope = rememberCoroutineScope()

    val gameState by when (game) {
        is HistoryGame -> derivedStateOf { game.finalGameState }
        is LiveGame -> {
            game.controls.observer.stateFlow.collectAsState()
        }
    }
    val input by derivedStateOf { (game as? LiveGame)?.controls?.input }

    val orientation = remember { mutableStateOf(
        input?.playerColor
                ?: (game as? ServiceGame)?.userPlayedAs
                ?: PlayerColor.WHITE
    ) }
    val makeMoveLoading = remember { mutableStateOf(false) }
    val boardStateBrowser = rememberListBrowser(derivedStateOf { gameState.boardStates })

    var resignDialogVisible by remember { mutableStateOf(false) }
    val resignation by derivedStateOf {
        input.takeIf { gameState.progress is GameProgress.Running }?.let { resignInput ->
            object : GameWindowState.Resignation {
                override val dialogVisible = resignDialogVisible

                override fun openDialog() {
                    resignDialogVisible = true
                }

                override fun cancelResignation() {
                    resignDialogVisible = false
                }

                override fun confirmResignation() {
                    coroutineScope.launch {
                        // TODO: Handle errors, needed in case we introduce a client-server architecture
                        resignInput.resign()
                    }
                    resignDialogVisible = false
                }
            }
        }
    }

    val fenPinned = remember { mutableStateOf(false) }

    return object : GameWindowState {
        override val game
            get() = game

        override val gameState
            get() = gameState

        override val orientation by orientation

        override fun flipOrientation() {
            orientation.value = orientation.value.opponent
        }

        override val resignation
            get() = resignation

        override val moveEnabledForColor
            get() = input?.let { input ->
                input.playerColor.takeIf {
                    !makeMoveLoading.value &&
                        gameState.progress is GameProgress.Running &&
                        boardStateBrowser.lastSelected &&
                        gameState.currentState.currentTurn == input.playerColor
                }
            }

        override val waitingForOwnMove
            get() = gameState.progress is GameProgress.Running &&
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

        override var fenPinned by fenPinned
    }
}
