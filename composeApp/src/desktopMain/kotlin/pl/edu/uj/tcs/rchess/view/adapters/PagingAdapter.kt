package pl.edu.uj.tcs.rchess.view.adapters

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.view.shared.ErrorCard
import pl.edu.uj.tcs.rchess.view.shared.Loading
import pl.edu.uj.tcs.rchess.viewmodel.paging.Paging

/**
 * A Composable that displays a list based on the [Paging] state.
 *
 * The item list is displayed using a [LazyColumn]. The rendering composable is provided by the [listContent] parameter.
 * If the list is empty it displays the [emptyListContent] instead.
 *
 * This Composable handles the loading and errors states. The UI is different when the list is empty and when it's not.
 *
 * If an error occurs during data loading, an [ErrorCard] is displayed with the [errorHeader].
 * Fetching is paused until the error is dismissed by the user.
 *
 * The list only fetches more pages when the user has scrolled near the end of the list.
 */
@Composable
fun <T> PagingAdapter(
    paging: Paging<T>,
    loadingMessage: String,
    errorHeader: String,
    contentPadding: PaddingValues,
    statusPadding: PaddingValues = PaddingValues(0.dp),
    listContent: LazyListScope.(list: List<T>) -> Unit,
    emptyListContent: @Composable () -> Unit,
) {
    val scrollState = rememberLazyListState()
    paging.onReload { scrollState.scrollToItem(0) }
    val items by paging.collectListAsState(derivedStateOf {
        val lastVisibleIndex = scrollState.firstVisibleItemIndex + scrollState.layoutInfo.visibleItemsInfo.size
        val extraFetched = scrollState.layoutInfo.totalItemsCount - lastVisibleIndex
        extraFetched < (3 * scrollState.layoutInfo.visibleItemsInfo.size).coerceIn(10, 50)
    })

    Column {
        if (items.isEmpty()) {
            val error = paging.error
            if (error != null) {
                Box(
                    modifier = Modifier.padding(statusPadding).fillMaxWidth().weight(1f),
                ) {
                    ErrorCard(
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                        headerText = errorHeader,
                        error = error,
                        dismissText = "Retry",
                        onDismiss = paging::dismissError,
                        prominent = true,
                    )
                }
            } else if (paging.loading) {
                Loading(
                    modifier = Modifier.padding(statusPadding).fillMaxWidth().weight(1f),
                    text = loadingMessage,
                )
            } else {
                Box(Modifier.padding(statusPadding).fillMaxWidth().weight(1f)) {
                    emptyListContent()
                }
            }

            return@Column
        }

        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxHeight().weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = scrollState,
                contentPadding = contentPadding,
            ) {
                listContent(items)

                if (paging.loading) {
                    item {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
            if (scrollState.canScrollForward || scrollState.canScrollBackward) {
                VerticalScrollbar(
                    modifier = Modifier.padding(
                        start = 8.dp,
                        top = contentPadding.calculateTopPadding(),
                        bottom = contentPadding.calculateBottomPadding(),
                    ).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(scrollState)
                )
            }
        }

        paging.error?.let { error ->
            HorizontalDivider()

            ErrorCard(
                modifier = Modifier
                    .padding(statusPadding)
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                headerText = errorHeader,
                dismissText = "Retry",
                error = error,
                onDismiss = paging::dismissError,
            )
        }
    }
}
