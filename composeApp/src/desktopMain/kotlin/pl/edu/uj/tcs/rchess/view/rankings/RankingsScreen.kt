package pl.edu.uj.tcs.rchess.view.rankings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.view.datastate.DataStateScreen
import pl.edu.uj.tcs.rchess.view.shared.PlaceholderScreen
import pl.edu.uj.tcs.rchess.viewmodel.AppContext

@Composable
fun RankingsScreen(context: AppContext) {
    val viewModel = context.rankingListViewModel

    DataStateScreen(viewModel.rankingList, "Loading ranking list") { rankings, refresh ->
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .widthIn(max = 360.dp)
                    .selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(rankings) { ranking ->
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
