package pl.edu.uj.tcs.rchess.view.rankings

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.api.entity.ranking.RankingSpot
import pl.edu.uj.tcs.rchess.view.shared.PlayerName

@Composable
fun RankingSpotItem(rankingSpot: RankingSpot) {
    Row(
        modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth().height(36.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "#${rankingSpot.placement}",
            modifier = Modifier.widthIn(min = 64.dp),
        )

        Text(
            "${rankingSpot.elo}",
            modifier = Modifier.widthIn(min = 64.dp),
        )

        PlayerName(
            modifier = Modifier.weight(1f),
            player = rankingSpot.serviceAccount,
        )
    }
}
