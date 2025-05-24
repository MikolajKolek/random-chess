package pl.edu.uj.tcs.rchess.view.gamesidebar

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

enum class Tab(val displayName: String) {
    MOVES("Moves"),
    INFO("Info"),
    EXPORT("Export"),
}

@Composable
fun GameSidebar(
    modifier: Modifier
) {
    var tab by remember { mutableStateOf(Tab.MOVES) }
    Column(
        modifier = modifier,
    ) {
        @OptIn(ExperimentalMaterial3Api::class)
        PrimaryTabRow(selectedTabIndex = tab.ordinal) {
            Tab.entries.forEachIndexed { index, tabItem ->
                Tab(
                    selected = tab == tabItem,
                    onClick = { tab = tabItem },
                    text = { Text(tabItem.displayName) },
                )
            }
        }

        when (tab) {
            Tab.MOVES -> MovesTab(modifier = Modifier.weight(1f))
            Tab.INFO -> InfoTab(modifier = Modifier.weight(1f))
            Tab.EXPORT -> ExportTab(modifier = Modifier.weight(1f))
        }
    }
}
