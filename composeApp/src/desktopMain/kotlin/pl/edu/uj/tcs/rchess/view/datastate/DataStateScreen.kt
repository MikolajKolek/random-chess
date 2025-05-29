package pl.edu.uj.tcs.rchess.view.datastate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import pl.edu.uj.tcs.rchess.view.shared.Loading
import pl.edu.uj.tcs.rchess.viewmodel.datastate.DataState
import pl.edu.uj.tcs.rchess.viewmodel.datastate.DataStateViewModel

@Composable
fun <T> DataStateScreen(
    viewModel: DataStateViewModel<T>,
    dataLoadingMessage: String,
    content: @Composable (data: T, refresh: () -> Unit) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    when (val stateCopy = state) {
        is DataState.Loading -> Loading(text = dataLoadingMessage)
        is DataState.Error -> ErrorScreen(stateCopy.error, onRefresh = viewModel::refresh)
        is DataState.Success -> content(stateCopy.data, viewModel::refresh)
    }
}
