package pl.edu.uj.tcs.rchess.view.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.game.GameInput
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import pl.edu.uj.tcs.rchess.model.state.GameState
import pl.edu.uj.tcs.rchess.view.board.BoardArea
import pl.edu.uj.tcs.rchess.view.gamesidebar.ExportTab
import pl.edu.uj.tcs.rchess.view.gamesidebar.GameSidebar
import pl.edu.uj.tcs.rchess.view.gamesidebar.InfoTab
import pl.edu.uj.tcs.rchess.view.gamesidebar.MovesTab
import pl.edu.uj.tcs.rchess.view.gamesidebar.Progress
import pl.edu.uj.tcs.rchess.view.gamesidebar.Tab
import pl.edu.uj.tcs.rchess.viewmodel.rememberGameViewState
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.icon_chevron_next
import rchess.composeapp.generated.resources.icon_chevron_prev
import rchess.composeapp.generated.resources.icon_resign
import rchess.composeapp.generated.resources.icon_swap_vert

@Composable
fun GameScreen(
    gameState: GameState,
    input: GameInput?,
) {
    val state = rememberGameViewState(gameState, input)

    val boardStateIndex = remember { mutableStateOf(0) }
    if (boardStateIndex.value >= gameState.boardStates.size) {
        boardStateIndex.value = gameState.boardStates.size - 1
    }
    val boardState = gameState.boardStates[boardStateIndex.value]

    val isInitial = boardStateIndex.value == 0
    val isCurrent = boardStateIndex.value == gameState.boardStates.size - 1

    var lastBoardStateSize by remember { mutableStateOf(gameState.boardStates.size) }
    LaunchedEffect(gameState.boardStates.size) {
        if (boardStateIndex.value == lastBoardStateSize - 1) {
            boardStateIndex.value = gameState.boardStates.size - 1
        }
        lastBoardStateSize = gameState.boardStates.size
    }

    val coroutineScope = rememberCoroutineScope()
    var makeMoveLoading by remember { mutableStateOf(false) }
    fun tryMakeMove(move: Move) {
        input?.let {
            if (makeMoveLoading) return
            makeMoveLoading = true
            // TODO: Should this happen in the Dispatchers.IO scope?
            coroutineScope.launch {
                // TODO: Handle errors, needed in case we introduce a client-server architecture
                try {
                    it.makeMove(move)
                } finally {
                    makeMoveLoading = false
                }
            }
        }
    }

    Row {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background),
        ) {
            BoardArea(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                state = boardState,
                orientation = state.orientation,
                moveEnabledForColor = input
                    ?.takeIf { isCurrent && gameState.progress is GameProgress.Running }
                    ?.playerColor,
                onMove = ::tryMakeMove,
                whiteClock = gameState.getPlayerClock(PlayerColor.WHITE),
                blackClock = gameState.getPlayerClock(PlayerColor.BLACK),
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxHeight(),
            ) {
                @OptIn(ExperimentalMaterial3Api::class)
                @Composable
                fun TooltipIconButton(
                    onClick: () -> Unit,
                    tooltip: String,
                    enabled: Boolean = true,
                    icon: @Composable () -> Unit,
                ) {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = { PlainTooltip { Text(tooltip) } },
                        state = rememberTooltipState()
                    ) {
                        IconButton(
                            enabled = enabled,
                            onClick = onClick,
                        ) {
                            icon()
                        }
                    }

                }

                Spacer(modifier = Modifier.weight(1f))

                state.resignation?.let {
                    TooltipIconButton(
                        onClick = it::openDialog,
                        tooltip = "Resign",
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_resign),
                            contentDescription = "Resign",
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))
                }

                TooltipIconButton(
                    onClick = state::flipOrientation,
                    tooltip = "Rotate board",
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.icon_swap_vert),
                        contentDescription = "Rotate board",
                    )
                }

                TooltipIconButton(
                    enabled = !isInitial,
                    onClick = {
                        boardStateIndex.value--
                    },
                    tooltip = "Previous move",
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.icon_chevron_prev),
                        contentDescription = "Previous move",
                    )
                }

                TooltipIconButton(
                    enabled = !isCurrent,
                    onClick = {
                        boardStateIndex.value++
                    },
                    tooltip = "Next move",
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.icon_chevron_next),
                        contentDescription = "Next move",
                    )
                }
            }
        }

        VerticalDivider()

        GameSidebar(
            modifier = Modifier
                .width(384.dp)
                .fillMaxHeight(),
            displayTab = { tab ->
                when (tab) {
                    Tab.MOVES -> MovesTab(
                        fullMoves = gameState.fullMoves,
                        boardStateIndex = boardStateIndex.value,
                        onSelectIndex = { index ->
                            boardStateIndex.value = index
                        }
                    )

                    Tab.INFO -> InfoTab()
                    Tab.EXPORT -> ExportTab(
                        currentBoardState = boardState,
                    )
                }
            },
            displayProgress = {
                Progress(
                    gameState,
                    currentBoardStateSelected = isCurrent,
                    onSelectCurrent = {
                        boardStateIndex.value = gameState.boardStates.size - 1
                    },
                    waitingForOwnMove = gameState.progress is GameProgress.Running && gameState.currentState.currentTurn == input?.playerColor,
                )
            },
        )
    }
}
