package pl.edu.uj.tcs.rchess.view.shared

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScrollableColumn(
    modifier: Modifier = Modifier,
    leftPadding: Boolean,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit,
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(contentPadding),
            verticalArrangement = verticalArrangement,
        ) {
            content()
        }

        if (scrollState.canScrollForward || scrollState.canScrollBackward) {
            VerticalScrollbar(
                modifier = Modifier.fillMaxHeight().padding(start = if (leftPadding) 8.dp else 0.dp, end = 4.dp),
                adapter = rememberScrollbarAdapter(scrollState)
            )
        }
    }
}
