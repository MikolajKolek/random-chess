package pl.edu.uj.tcs.rchess.components.datastate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun <T> DataStateScreen(
    loadData: suspend () -> T,
    content: @Composable (data: T, refresh: () -> Unit) -> Unit
) {
    val viewModel = remember { DataStateViewModel(loadData) }
    val state by viewModel.state.collectAsState()

    val stateCopy = state
    when (stateCopy) {
        is DataState.Loading -> LoadingScreen()
        is DataState.Error -> ErrorScreen(stateCopy.error, onRefresh = viewModel::refresh)
        is DataState.Success -> content(stateCopy.data, viewModel::refresh)
    }
}
