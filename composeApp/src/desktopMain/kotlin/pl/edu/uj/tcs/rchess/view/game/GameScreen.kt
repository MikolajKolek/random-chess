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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.view.board.BoardArea
import pl.edu.uj.tcs.rchess.view.gamesidebar.GameSidebar
import pl.edu.uj.tcs.rchess.view.gamesidebar.InfoTab
import pl.edu.uj.tcs.rchess.view.gamesidebar.MovesTab
import pl.edu.uj.tcs.rchess.view.gamesidebar.Progress
import pl.edu.uj.tcs.rchess.view.gamesidebar.Tab
import pl.edu.uj.tcs.rchess.viewmodel.GameWindowState
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.icon_chevron_next
import rchess.composeapp.generated.resources.icon_chevron_prev
import rchess.composeapp.generated.resources.icon_resign
import rchess.composeapp.generated.resources.icon_swap_vert

@Composable
fun GameScreen(
    windowState: GameWindowState,
) = windowState.run {
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
                state = boardStateBrowser.current,
                orientation = orientation,
                moveEnabledForColor = moveEnabledForColor,
                onMove = ::makeMove,
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
