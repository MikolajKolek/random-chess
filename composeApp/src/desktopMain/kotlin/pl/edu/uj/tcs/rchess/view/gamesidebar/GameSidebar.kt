package pl.edu.uj.tcs.rchess.view.gamesidebar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

enum class Tab(val displayName: String) {
    MOVES("Moves"),
    INFO("Info"),
}

@Composable
fun GameSidebar(
    modifier: Modifier,
    displayTab: @Composable (Tab) -> Unit,
    displayFooter: @Composable () -> Unit,
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

        Box(
            modifier = Modifier.weight(1f),
        ) {
            displayTab(tab)
        }

        HorizontalDivider()

        displayFooter()
    }
}
