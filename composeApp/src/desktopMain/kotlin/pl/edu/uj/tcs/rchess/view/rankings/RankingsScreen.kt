package pl.edu.uj.tcs.rchess.view.rankings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.view.adapters.DataStateAdapter
import pl.edu.uj.tcs.rchess.view.adapters.PagingAdapter
import pl.edu.uj.tcs.rchess.view.shared.PlaceholderScreen
import pl.edu.uj.tcs.rchess.view.shared.ScrollableColumn
import pl.edu.uj.tcs.rchess.viewmodel.AppContext
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.icon_refresh

@Composable
fun RankingsScreen(context: AppContext) {
    val viewModel = context.rankingListViewModel

    DataStateAdapter(
        viewModel.rankingList,
        "Loading ranking list",
        "An error occurred while loading ranking list",
    ) { rankings, refresh ->
        Row(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxSize()
        ) {
            ScrollableColumn(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .widthIn(max = 360.dp)
                    .selectableGroup(),
                contentPadding = PaddingValues(vertical = 16.dp),
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
                PlaceholderScreen(
                   text = "Select ranking"
                )
                return@DataStateAdapter
            }

            Card(Modifier.padding(vertical = 16.dp)) {
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

                    TextButton(onClick = { viewModel.selectedRankingPaging?.refresh() }) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_refresh),
                            contentDescription = "Refresh",
                            modifier = Modifier.padding(end = 8.dp),
                        )

                        Text("Refresh")
                    }
                }

                HorizontalDivider()

                PagingAdapter(
                    paging,
                    "Loading ranking placements...",
                    "An error occurred while loading ranking placements",
                    contentPadding = PaddingValues(bottom = 24.dp),
                    statusPadding = PaddingValues(all = 16.dp),
                    listContent = { list ->
                        items(list) { spot ->
                            RankingSpotItem(spot)
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
}
