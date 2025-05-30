package pl.edu.uj.tcs.rchess.view.board

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.state.ClockState
import pl.edu.uj.tcs.rchess.view.shared.PlayerName
import kotlin.time.Duration

data class DisplayedTime(
    val clock: Duration,
    val extra: Duration,
)

@Composable
fun remainingTime(clockState: ClockState): DisplayedTime {
    fun getDisplayedTime() = DisplayedTime(
        clock = clockState.remainingTimeOnClock(),
        extra = clockState.remainingExtraTime(),
    )

    var remainingTime by remember { mutableStateOf(getDisplayedTime()) }

    LaunchedEffect(clockState) {
        remainingTime = getDisplayedTime()
        if (clockState is ClockState.Running) {
            while (true) {
                withFrameNanos {
                    remainingTime = getDisplayedTime()
                }
            }
        }
    }

    return remainingTime
}

fun Duration.formatHuman(alwaysShowMinutes: Boolean = true) =
    toComponents { hours, minutes, seconds, nanoseconds ->
        val centisecond = nanoseconds / 10_000_000

        when {
            hours > 0 -> {
                "%d:%02d:%02d.%02d".format(hours, minutes, seconds, centisecond)
            }
            minutes > 0 || alwaysShowMinutes -> {
                "%d:%02d.%02d".format(minutes, seconds, centisecond)
            }
            else -> {
                "%02d.%02d".format(seconds, centisecond)
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerBar(
    modifier: Modifier = Modifier,
    color: PlayerColor,
    name: String,
    clockState: ClockState?,
    isWinner: Boolean,
) {
    Row(
        modifier = Modifier.height(PlayerBar.height).then(modifier)
    ) {
        PlayerName(name = name, color = color, isWinner = isWinner)

        Spacer(
            modifier = Modifier.weight(1f)
        )

        if (clockState != null) {
            val remainingTime = remainingTime(clockState)

            Text(
                remainingTime.clock.formatHuman(),
                fontSize = 20.sp
            )

            remainingTime.extra.takeIf { it > Duration.ZERO }?.let {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = { PlainTooltip { Text("Additional time before first move") } },
                    state = rememberTooltipState()
                ) {
                    Text(
                        "+${it.formatHuman(alwaysShowMinutes = false)}",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

object PlayerBar {
    val height = 24.dp
}
