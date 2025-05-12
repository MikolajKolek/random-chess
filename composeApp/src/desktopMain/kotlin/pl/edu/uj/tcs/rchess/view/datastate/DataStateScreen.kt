package pl.edu.uj.tcs.rchess.view.datastate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.edu.uj.tcs.rchess.viewmodel.datastate.DataState
import pl.edu.uj.tcs.rchess.viewmodel.datastate.DataStateViewModel

@Composable
fun <T> DataStateScreen(
    loadData: suspend () -> T,
    viewModel: DataStateViewModel<T> = viewModel { DataStateViewModel(loadData) },
    content: @Composable (data: T, refresh: () -> Unit) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    val stateCopy = state
    when (stateCopy) {
        is DataState.Loading -> LoadingScreen()
        is DataState.Error -> ErrorScreen(stateCopy.error, onRefresh = viewModel::refresh)
        is DataState.Success -> content(stateCopy.data, viewModel::refresh)
    }
}
