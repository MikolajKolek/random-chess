package pl.edu.uj.tcs.rchess.view.gamesidebar

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.api.entity.ranking.EloUpdate
import pl.edu.uj.tcs.rchess.api.entity.ranking.RankingUpdate
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.pieces.King
import pl.edu.uj.tcs.rchess.view.board.icon
import pl.edu.uj.tcs.rchess.view.shared.formatWithSign

@Composable
private fun PlayerEloUpdate(modifier: Modifier, eloUpdate: EloUpdate?, color: PlayerColor) {
    if (eloUpdate == null) {
        Text(
            "not ranked",
            textAlign = TextAlign.Center,
            modifier = modifier,
        )
        return
    }

    val style = MaterialTheme.typography.bodyMedium.run {
        copy(fontFeatureSettings = "$fontFeatureSettings, case 1")
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(
            modifier = Modifier.size(20.dp),
            painter = painterResource(King(color).icon),
            contentDescription = null,
        )

        Text(
            "${eloUpdate.oldElo} → ${eloUpdate.newElo}",
            style = style,
            modifier = Modifier.weight(1f),
        )

        Text(
            (eloUpdate.newElo - eloUpdate.oldElo).formatWithSign(),
            style = style,
            textAlign = TextAlign.Right,
        )
    }
}


@Composable
fun RankingChanges(
    changes: List<RankingUpdate>,
    onSelectRanking: (rankingId: Int) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        changes.forEach {
            Column {
                Text(
                    text = it.ranking.name,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .fillMaxWidth()
                        .clickable { onSelectRanking(it.ranking.id) },
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PlayerEloUpdate(Modifier.weight(1f), it.whiteEloUpdate, PlayerColor.WHITE)
                    PlayerEloUpdate(Modifier.weight(1f), it.blackEloUpdate, PlayerColor.BLACK)
                }
            }
        }
    }
}