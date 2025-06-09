package pl.edu.uj.tcs.rchess.view.gamesidebar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
