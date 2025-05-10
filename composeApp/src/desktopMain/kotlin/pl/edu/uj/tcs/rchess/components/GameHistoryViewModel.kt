package pl.edu.uj.tcs.rchess.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.edu.uj.tcs.rchess.server.ClientApi
import pl.edu.uj.tcs.rchess.server.HistoryGame

class GameHistoryViewModel(private val clientApi: ClientApi) : ViewModel() {
    private val _dataState = MutableStateFlow<DataState<List<HistoryGame>>>(DataState.Loading())
    val gameHistory: StateFlow<DataState<List<HistoryGame>>> = _dataState

    private fun load() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    _dataState.value = DataState.Success(clientApi.getUserGames())
                } catch (e: Exception) {
                    _dataState.value = DataState.Error(e)
                }
            }
        }
    }

    fun refresh() {
        if (_dataState.value is DataState.Loading) return

        load()
    }

    init {
        load()
    }
}
