package pl.edu.uj.tcs.rchess.view.rankings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.view.adapters.DataStateAdapter
import pl.edu.uj.tcs.rchess.view.adapters.PagingAdapter
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

            val paging = viewModel.selectedRankingPaging
            if (paging == null) {
                Card(Modifier.padding(bottom = 16.dp)) {
                    PlaceholderScreen(
                        text = "Ranking details"
                    )
                }
                return@DataStateAdapter
            }

            PagingAdapter(
                paging,
                "Loading ranking placements...",
                "An error occurred while loading ranking placements",
                contentPadding = PaddingValues(bottom = 24.dp),
                listContent = { list ->
                    items(list) { spot ->
                        Text("${spot.placement}. ${spot.serviceAccount.displayName} - ${spot.elo}")
                    }
                },
                emptyListContent = {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = "This ranking is empty"
                    )
                },
            )
        }
    }
}
