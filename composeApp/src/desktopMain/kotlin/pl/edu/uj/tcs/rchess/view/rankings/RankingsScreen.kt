package pl.edu.uj.tcs.rchess.view.rankings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.view.adapters.DataStateAdapter
import pl.edu.uj.tcs.rchess.view.shared.PlaceholderScreen
import pl.edu.uj.tcs.rchess.view.shared.ScrollableColumn
import pl.edu.uj.tcs.rchess.viewmodel.AppContext

@Composable
fun RankingsScreen(context: AppContext) {
    val viewModel = context.rankingListViewModel

    DataStateAdapter(
        viewModel.rankingList,
        "Loading ranking list",
        "An error occurred while loading ranking list",
    ) { rankings, refresh ->
        Row(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp).fillMaxSize()
        ) {
            ScrollableColumn(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .widthIn(max = 360.dp)
                    .selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                leftPadding = true,
            ) {
                rankings.forEach { ranking ->
                    RankingListItem(
                        modifier = Modifier.fillMaxWidth(),
                        ranking,
                        ranking.id == viewModel.selectedRankingId,
                        onClick = {
                            viewModel.selectRanking(ranking.id)
                        },
                    )
                }
            }

            Card {
                PlaceholderScreen(
                    text = "Ranking details"
                )
            }
        }
    }
}
