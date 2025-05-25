package pl.edu.uj.tcs.rchess.view.board

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.state.ClockState
import kotlin.time.Duration

@Composable
fun remainingTime(clockState: ClockState): Duration {
    var remainingTime by remember { mutableStateOf(clockState.remainingTimeOnClock()) }

    LaunchedEffect(clockState) {
        remainingTime = clockState.remainingTimeOnClock()
        if (clockState is ClockState.Running) {
            while (true) {
                withFrameNanos {
                    remainingTime = clockState.remainingTimeOnClock()
                }
            }
        }
    }

    return maxOf(remainingTime, Duration.ZERO)
}

fun Duration.formatHuman() =
    toComponents { hours, minutes, seconds, nanoseconds ->
        val centisecond = nanoseconds / 10_000_000

        if (hours > 0) {
            "%d:%02d:%02d.%02d".format(hours, minutes, seconds, centisecond)
        } else {
            "%d:%02d.%02d".format(minutes, seconds, centisecond)
        }
    }

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
        modifier = Modifier.height(PlayerBar.height).then(modifier)
    ) {
        Text(color.unicodeSymbol)

        Text(name ?: when (color) {
            PlayerColor.WHITE -> "White player"
            PlayerColor.BLACK -> "Black player"
        })

        Spacer(
            modifier = Modifier.weight(1f)
        )

        if (clockState != null) {
            val remainingTime = remainingTime(clockState)

            Text(
                remainingTime.formatHuman(),
                fontSize = 20.sp
            )
        }
    }
}

object PlayerBar {
    val height = 24.dp
}
