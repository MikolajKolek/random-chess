package pl.edu.uj.tcs.rchess.view.gamesidebar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.edu.uj.tcs.rchess.model.SanFullMove

@Composable
fun MovesTab(
    modifier: Modifier = Modifier,
    fullMoves: List<SanFullMove>,
    boardStateIndex: Int,
    onSelectIndex: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .then(modifier)
    ) {
        fullMoves.forEach { fullMove ->
            Row(
                Modifier.height(36.dp)
            ) {
                Box(
                    modifier.fillMaxHeight().width(48.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        "${fullMove.number}.",
                        modifier = Modifier.padding(start = 16.dp),
                        fontSize = 18.sp,
                    )
                }

                @Composable
                fun HalfMove(halfMove: SanFullMove.HalfMove?) {
                    if (halfMove == null) {
                        Spacer(Modifier.weight(1f))
                        return
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable {
                                onSelectIndex(halfMove.index)
                            }
                            .run {
                                if (halfMove.index == boardStateIndex) {
                                    background(color = MaterialTheme.colorScheme.primaryContainer)
                                } else this
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

                HalfMove(fullMove.white)
                HalfMove(fullMove.black)
            }
        }
    }
}
