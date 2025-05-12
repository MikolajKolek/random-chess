package pl.edu.uj.tcs.rchess.viewmodel.datastate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DataStateViewModel<T>(
    private val loadData: suspend () -> T,
) : ViewModel() {
    private val _state = MutableStateFlow<DataState<T>>(DataState.Loading())
    val state: StateFlow<DataState<T>> = _state

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

    fun refresh() {
        if (_state.value is DataState.Loading) return
        _state.value = DataState.Loading()
        load()
    }

    init {
        load()
    }
}
