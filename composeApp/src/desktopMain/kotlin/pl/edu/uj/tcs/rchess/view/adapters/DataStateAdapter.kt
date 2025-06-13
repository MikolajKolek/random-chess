package pl.edu.uj.tcs.rchess.view.adapters

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.view.shared.ErrorCard
import pl.edu.uj.tcs.rchess.view.shared.Loading
import pl.edu.uj.tcs.rchess.viewmodel.datastate.DataState
import pl.edu.uj.tcs.rchess.viewmodel.datastate.DataStateViewModel

/**
 * A Composable that displays the screen according to the state of the [DataStateViewModel].
 *
 * If the state is:
 * - [DataState.Loading], it shows the [dataLoadingMessage] and a loading indicator.
 * - [DataState.Error], it shows an [ErrorCard] with the [errorHeader] and a retry button.
 * - [DataState.Success], the [content] functions is called with the loaded data.
 *   The [content] lambda also receives a `refresh` function that calls the [DataStateViewModel.refresh] method.
 */
@Composable
fun <T> DataStateAdapter(
    viewModel: DataStateViewModel<T>,
    dataLoadingMessage: String,
    errorHeader: String,
    content: @Composable (data: T, refresh: () -> Unit) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    when (val stateCopy = state) {
        is DataState.Loading -> Loading(text = dataLoadingMessage)
        is DataState.Error -> Box(Modifier.padding(16.dp).fillMaxSize()) {
            ErrorCard(
                modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                headerText = errorHeader,
                error = stateCopy.error,
                onDismiss = viewModel::refresh,
                prominent = true,
                dismissText = "Retry",
            )
        }
        is DataState.Success -> content(stateCopy.data, viewModel::refresh)
    }
}
