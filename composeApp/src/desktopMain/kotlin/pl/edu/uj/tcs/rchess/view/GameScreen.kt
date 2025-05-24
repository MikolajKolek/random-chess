package pl.edu.uj.tcs.rchess.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.game.GameInput
import pl.edu.uj.tcs.rchess.model.state.GameState
import pl.edu.uj.tcs.rchess.view.board.BoardArea
import pl.edu.uj.tcs.rchess.view.gamesidebar.*
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.swap_vert

@Composable
fun GameScreen(
    gameState: GameState,
    input: GameInput?,
    // History browsing is disabled for live games, as it required stepping after each move
    // TODO: Fix
    enableBrowsing: Boolean = true
) {
    val orientation = remember {
        mutableStateOf(input?.playerColor ?: PlayerColor.WHITE)
    }

    val boardStateIndex = remember { mutableStateOf(0) }
    if (enableBrowsing && boardStateIndex.value >= gameState.boardStates.size) {
        boardStateIndex.value = gameState.boardStates.size - 1
    }
    val boardState = if(enableBrowsing) gameState.boardStates[boardStateIndex.value] else gameState.currentState

    val isInitial = boardStateIndex.value == 0
    val isCurrent = if(enableBrowsing) boardStateIndex.value == gameState.boardStates.size - 1 else true

    Row {
        BoardArea(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            state = boardState,
            orientation = orientation.value,
            moveEnabledForColor = input?.takeIf { isCurrent }?.playerColor,
            onMove = { move ->
                input?.makeMove(move)
            },
            whiteClock = gameState.getPlayerClock(PlayerColor.WHITE),
            blackClock = gameState.getPlayerClock(PlayerColor.BLACK),
        )

        Column(
            modifier = Modifier.padding(16.dp).fillMaxHeight()
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

            if (enableBrowsing) {
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

        GameSidebar(
            modifier = Modifier.width(512.dp).fillMaxHeight(),
        ) { tab ->
            when (tab) {
                Tab.MOVES -> MovesTab(
                    fullMoves = gameState.fullMoves,
                    boardStateIndex = boardStateIndex.value,
                    onSelectIndex = { index ->
                        boardStateIndex.value = index
                    }
                )
                Tab.INFO -> InfoTab()
                Tab.EXPORT -> ExportTab()
            }
        }
    }
}
