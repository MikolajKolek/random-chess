package pl.edu.uj.tcs.rchess.view.rankings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import formatHumanSetting
import pl.edu.uj.tcs.rchess.api.entity.Ranking
import kotlin.time.Duration

@Composable
fun RankingListItem(
    ranking: Ranking,
    onClick: () -> Unit,
) {
    // TODO: This will no longer be needed when playtimeMax becomes not null
    //  and infinite durations are represented as Duration.INFINITE
    val playtimeMax = ranking.playtimeMax ?: Duration.INFINITE

    @OptIn(ExperimentalMaterialApi::class)
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),

        text = {
            Text(ranking.name)
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
