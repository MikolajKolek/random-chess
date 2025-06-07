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
                onRetry = viewModel::refresh,
                prominent = true,
            )
        }
        is DataState.Success -> content(stateCopy.data, viewModel::refresh)
    }
}
