package pl.edu.uj.tcs.rchess.view.rankings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import formatHumanSetting
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.api.entity.Ranking
import pl.edu.uj.tcs.rchess.util.runIf
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
    // TODO: This will no longer be needed when playtimeMax becomes not null
    //  and infinite durations are represented as Duration.INFINITE
    val playtimeMax = ranking.playtimeMax ?: Duration.INFINITE

    Card(
        modifier = Modifier.then(modifier),
        colors = CardDefaults.cardColors()
            .runIf(selected) {
                copy(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
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
                    modifier = Modifier.padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
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
