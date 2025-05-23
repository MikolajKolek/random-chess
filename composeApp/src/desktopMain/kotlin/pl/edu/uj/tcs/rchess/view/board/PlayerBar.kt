package pl.edu.uj.tcs.rchess.view.board

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.state.ClockState

/**
 * @param isSelf
 */
@Composable
fun PlayerBar(
    modifier: Modifier = Modifier,
    color: PlayerColor,
    name: String?,
    isSelf: Boolean,
    clockState: ClockState?,
) {
    Row(
        modifier = modifier.height(PlayerBar.height)
    ) {
        Text(color.unicodeSymbol)

        Text(name ?: when (color) {
            PlayerColor.WHITE -> "White player"
            PlayerColor.BLACK -> "Black player"
        })

        Spacer(
            modifier = Modifier.weight(1f)
        )

        when (clockState) {
            is ClockState.Paused -> {
                Text("Paused: ${clockState.remainingTime}")
            }
            is ClockState.Running -> {
                // TODO: Fix, this will not update
                Text("Running: ${clockState.remainingTime()}")
            }
            null -> {}
        }
    }
}

object PlayerBar {
    val height = 24.dp
}
