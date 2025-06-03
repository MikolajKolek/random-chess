package pl.edu.uj.tcs.rchess.view.gamesidebar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.edu.uj.tcs.rchess.model.SanFullMove
import pl.edu.uj.tcs.rchess.util.runIf
import pl.edu.uj.tcs.rchess.view.shared.ScrollableColumn

@Composable
fun MovesTab(
    modifier: Modifier = Modifier,
    fullMoves: List<SanFullMove>,
    boardStateIndex: Int,
    onSelectIndex: (Int) -> Unit,
) {
    ScrollableColumn(
        modifier = modifier,
        leftPadding = true,
    ) {
        @Composable
        fun FullMoveRow(
            number: Int,
            content: @Composable RowScope.() -> Unit,
        ) {
            Row(
                Modifier.height(36.dp)
            ) {
                Box(
                    Modifier.fillMaxHeight().width(48.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        "${number}.",
                        modifier = Modifier.padding(start = 16.dp),
                        fontSize = 18.sp,
                    )
                }

                content()
            }
        }

        FullMoveRow(0) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        onSelectIndex(0)
                    }
                    .runIf(0 == boardStateIndex) {
                        background(color = MaterialTheme.colorScheme.primaryContainer)
                    },
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "Initial position",
                    fontSize = 18.sp,
                )
            }
        }

        @Composable
        fun RowScope.HalfMove(halfMove: SanFullMove.HalfMove?) {
            if (halfMove == null) {
                Spacer(Modifier.weight(1f))
                return
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        onSelectIndex(halfMove.moveIndex + 1)
                    }
                    .runIf(halfMove.moveIndex + 1 == boardStateIndex) {
                        background(color = MaterialTheme.colorScheme.primaryContainer)
                    },
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = halfMove.san,
                    fontSize = 18.sp,
                )
            }
        }

        fullMoves.forEach { fullMove ->
            FullMoveRow(fullMove.number) {
                HalfMove(fullMove.white)
                HalfMove(fullMove.black)
            }
        }
    }
}
