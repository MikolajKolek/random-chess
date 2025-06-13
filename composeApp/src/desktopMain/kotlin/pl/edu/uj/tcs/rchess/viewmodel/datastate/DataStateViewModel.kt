package pl.edu.uj.tcs.rchess.viewmodel.datastate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A generic view model that manages state handling for screens and windows
 * that show data fetched from the server or other sources.
 *
 * The state is represented by the [DataState] sealed class.
 *
 * Can be used with [pl.edu.uj.tcs.rchess.view.adapters.DataStateAdapter].
 *
 * @param loadData A suspend function that fetches data of type [T] or throws an exception.
 */
class DataStateViewModel<T>(
    private val loadData: suspend () -> T,
) : ViewModel() {
    private val _state = MutableStateFlow<DataState<T>>(DataState.Loading())

    private fun load() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    _state.value = DataState.Success(loadData())
                } catch (e: Exception) {
                    _state.value = DataState.Error(e)
                }
            }
        }
    }

    init {
        load()
    }

    val state: StateFlow<DataState<T>> = _state

    /**
     * Clears the error, sets the [state] to [DataState.Loading] and tries to fetch the data using [loadData].
     */
    fun refresh() {
        if (_state.value is DataState.Loading) return
        _state.value = DataState.Loading()
        load()
    }
}
