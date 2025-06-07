package pl.edu.uj.tcs.rchess.view.adapters

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import pl.edu.uj.tcs.rchess.view.shared.ErrorScreen
import pl.edu.uj.tcs.rchess.view.shared.Loading
import pl.edu.uj.tcs.rchess.viewmodel.datastate.DataState
import pl.edu.uj.tcs.rchess.viewmodel.datastate.DataStateViewModel

@Composable
fun <T> DataStateAdapter(
    viewModel: DataStateViewModel<T>,
    dataLoadingMessage: String,
    content: @Composable (data: T, refresh: () -> Unit) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    when (val stateCopy = state) {
        is DataState.Loading -> Loading(text = dataLoadingMessage)
        is DataState.Error -> ErrorScreen(
            modifier = Modifier.fillMaxSize(),
            stateCopy.error,
            onRetry = viewModel::refresh,
        )
        is DataState.Success -> content(stateCopy.data, viewModel::refresh)
    }
}
