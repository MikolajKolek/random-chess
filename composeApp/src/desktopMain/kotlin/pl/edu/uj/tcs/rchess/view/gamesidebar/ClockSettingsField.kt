package pl.edu.uj.tcs.rchess.view.gamesidebar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.view.shared.formatHumanSetting

@Composable
fun ClockSettingsField(clock: ClockSettings) {
    val spacerSize = 32.dp

    Column(
        modifier = Modifier.width(IntrinsicSize.Max),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                "initial time",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(Modifier.width(spacerSize))
            Text(
                "increment",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                clock.startingTime.formatHumanSetting(),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                "+",
                modifier = Modifier.width(spacerSize),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                clock.moveIncrease.formatHumanSetting(),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}