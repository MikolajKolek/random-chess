package pl.edu.uj.tcs.rchess.view.rankings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
    ranking: Ranking,
    selected: Boolean,
    onClick: () -> Unit,
) {
    // TODO: This will no longer be needed when playtimeMax becomes not null
    //  and infinite durations are represented as Duration.INFINITE
    val playtimeMax = ranking.playtimeMax ?: Duration.INFINITE

    @OptIn(ExperimentalMaterialApi::class)
    ListItem(
        modifier = Modifier
            .selectable(selected, onClick = onClick)
            .runIf(selected) {
                background(MaterialTheme.colorScheme.primaryContainer)
            },

        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(ranking.name)
                if (ranking.includeBots) {
                    Icon(
                        painter = painterResource(Res.drawable.icon_robot),
                        contentDescription = "Ranking includes bots",
                        modifier = Modifier.padding(start = 8.dp).size(16.dp),
                    )
                }
            }
        },

        secondaryText = {
            Column(
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
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
    )
}
