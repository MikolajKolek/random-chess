package pl.edu.uj.tcs.rchess.view.rankings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.view.datastate.DataStateScreen
import pl.edu.uj.tcs.rchess.view.shared.PlaceholderScreen
import pl.edu.uj.tcs.rchess.viewmodel.AppContext

@Composable
fun RankingsScreen(context: AppContext) {
    DataStateScreen(context.rankingsViewModel, "Loading ranking list") { rankings, refresh ->
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.widthIn(max = 400.dp)
            ) {
                items(rankings) { ranking ->
                    @OptIn(ExperimentalMaterialApi::class)
                    ListItem {
                        Text(ranking.name)
                    }
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
