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

@Composable
fun <T> PagingAdapter(
    paging: Paging<T, *>,
    loadingMessage: String,
    errorHeader: String,
    contentPadding: PaddingValues,
    listContent: LazyListScope.(list: List<T>) -> Unit,
    emptyListContent: @Composable () -> Unit,
) {
    val scrollState = rememberLazyListState()
    // TODO: Accept requests only when the games are near
    val items by paging.collectListAsState(derivedStateOf { true })

    Column {
        if (items.isEmpty()) {
            val error = paging.error
            if (error != null) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                ) {
                    ErrorCard(
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                        headerText = errorHeader,
                        error = error,
                        onRetry = paging::dismissError,
                        prominent = true,
                    )
                }
            } else if (paging.loading) {
                Loading(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    text = loadingMessage,
                )
            } else {
                Box(Modifier.fillMaxWidth().weight(1f)) {
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
                modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
                headerText = errorHeader,
                error = error,
                onRetry = paging::dismissError,
            )
        }
    }
}
