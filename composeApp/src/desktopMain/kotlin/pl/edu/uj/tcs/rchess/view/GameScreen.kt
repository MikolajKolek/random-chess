package pl.edu.uj.tcs.rchess.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.game.GameInput
import pl.edu.uj.tcs.rchess.model.state.GameState
import pl.edu.uj.tcs.rchess.view.board.BoardArea
import pl.edu.uj.tcs.rchess.view.board.Progress
import pl.edu.uj.tcs.rchess.view.gamesidebar.*
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.swap_vert

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

    var lastBoardStateSize by remember { mutableStateOf(gameState.boardStates.size) }
    LaunchedEffect(gameState.boardStates.size) {
        if (boardStateIndex.value == lastBoardStateSize - 1) {
            boardStateIndex.value = gameState.boardStates.size - 1
        }
        lastBoardStateSize = gameState.boardStates.size
    }

    // TODO: Introduce a view model
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
                orientation = orientation.value,
                moveEnabledForColor = input?.takeIf { isCurrent }?.playerColor,
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

                TooltipIconButton(
                    onClick = {
                        orientation.value = orientation.value.opponent
                    },
                    tooltip = "Rotate board",
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.swap_vert),
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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
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
                Progress(gameState)
            },
        )
    }
}
