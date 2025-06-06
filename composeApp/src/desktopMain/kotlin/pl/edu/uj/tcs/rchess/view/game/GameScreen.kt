package pl.edu.uj.tcs.rchess.view.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import pl.edu.uj.tcs.rchess.view.board.BoardArea
import pl.edu.uj.tcs.rchess.view.board.PlayerBar
import pl.edu.uj.tcs.rchess.view.gamesidebar.*
import pl.edu.uj.tcs.rchess.viewmodel.GameWindowState
import rchess.composeapp.generated.resources.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameScreen(
    windowState: GameWindowState,
) = windowState.run {
    Row {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
                .onPointerEvent(PointerEventType.Scroll) {
                    val change = it.changes.first()
                    // This works a bit wired on touchpads,
                    // because the scroll direction is not accounting for natural scrolling
                    // - users expect that moving two fingers up scrolls the view down.
                    // The scroll direction is thus inverted on touchpads compared to mouse input.
                    //
                    // Unfortunately, both mouse and touchpad input have [PointerType] == [Mouse],
                    // so we cannot distinguish them.
                    val delta = change.scrollDelta.y.toInt()
                    windowState.boardStateBrowser.selectDelta(delta)
                },
        ) {
            BoardArea(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                state = boardStateBrowser.current,
                orientation = orientation,
                moveEnabledForColor = moveEnabledForColor,
                onMove = ::makeMove,
                drawPlayerBar = { modifier, color ->
                    PlayerBar(
                        modifier = modifier,
                        color = color,
                        name = game.getPlayerName(color),
                        clockState = gameState.getPlayerClock(color),
                        isWinner = color == (gameState.progress as? GameProgress.Finished)?.result?.winner
                    )
                },
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

                resignation?.let {
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
                    onClick = ::flipOrientation,
                    tooltip = "Rotate board",
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.icon_swap_vert),
                        contentDescription = "Rotate board",
                    )
                }

                TooltipIconButton(
                    enabled = !boardStateBrowser.firstSelected,
                    onClick = boardStateBrowser::selectPrev,
                    tooltip = "Previous move",
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.icon_chevron_prev),
                        contentDescription = "Previous move",
                    )
                }

                TooltipIconButton(
                    enabled = !boardStateBrowser.lastSelected,
                    onClick = boardStateBrowser::selectNext,
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
                        boardStateIndex = boardStateBrowser.index,
                        onSelectIndex = boardStateBrowser::select
                    )
                    Tab.INFO -> InfoTab(
                        currentBoardState = boardStateBrowser.current,
                        game = game,
                        orientation = orientation,
                    )
                }
            },
            displayProgress = {
                Progress(
                    gameState,
                    currentBoardStateSelected = boardStateBrowser.lastSelected,
                    onSelectCurrent = boardStateBrowser::selectLast,
                    waitingForOwnMove = waitingForOwnMove,
                )
            },
        )
    }

    resignation?.takeIf { it.dialogVisible }?.run {
        ResignDialog(
            onConfirm = ::confirmResignation,
            onDismiss = ::cancelResignation,
        )
    }
}
