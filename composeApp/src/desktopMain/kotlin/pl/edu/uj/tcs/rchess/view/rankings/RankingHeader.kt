package pl.edu.uj.tcs.rchess.view.rankings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.icon_refresh

@Composable
fun RankingHeader(
    onRefresh: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(start = 24.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "Place",
            modifier = Modifier.widthIn(min = 64.dp),
            style = MaterialTheme.typography.labelLarge,
        )

        Text(
            "ELO",
            modifier = Modifier.widthIn(min = 64.dp),
            style = MaterialTheme.typography.labelLarge,
        )

        Text(
            "Player",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge,
        )

        TextButton(onClick = onRefresh) {
            Icon(
                painter = painterResource(Res.drawable.icon_refresh),
                contentDescription = "Refresh",
                modifier = Modifier.padding(end = 8.dp),
            )

            Text("Refresh")
        }
    }
}
