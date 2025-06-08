package pl.edu.uj.tcs.rchess.view.rankings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.api.entity.ranking.Ranking
import pl.edu.uj.tcs.rchess.util.runIf
import pl.edu.uj.tcs.rchess.view.shared.formatHumanSetting
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.icon_robot
import kotlin.time.Duration

@Composable
fun RankingListItem(
    modifier: Modifier = Modifier,
    ranking: Ranking,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val playtimeMax = ranking.playtimeMax

    Card(
        modifier = Modifier.then(modifier),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        )
            .runIf(selected) {
                copy(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .selectable(selected, onClick = onClick),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(bottom = 4.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(ranking.name)
                    if (ranking.includeBots) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_robot),
                            contentDescription = "Ranking includes bots",
                            modifier = Modifier.padding(start = 8.dp).size(16.dp),
                        )
                    }
                }

                Text(
                    when {
                        ranking.playtimeMin == Duration.ZERO && playtimeMax == Duration.INFINITE -> {
                            "Any clock settings"
                        }

                        ranking.playtimeMin == Duration.ZERO -> {
                            "Less than ${playtimeMax.formatHumanSetting()}"
                        }

                        ranking.playtimeMax == Duration.INFINITE -> {
                            "Over ${ranking.playtimeMin.formatHumanSetting()}"
                        }

                        else -> {
                            "${ranking.playtimeMin.formatHumanSetting()} – ${playtimeMax.formatHumanSetting()}"
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                )

                if (ranking.includeBots) {
                    Text(
                        "Includes bots",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}
