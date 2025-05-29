package pl.edu.uj.tcs.rchess.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.edu.uj.tcs.rchess.model.PlayerColor

class NewGameViewModel(private val context: AppContext): ViewModel() {
    /**
     * The color the user wants to play as.
     * null if the color should be picked randomly.
     */
    var startingPlayerColor by mutableStateOf<PlayerColor?>(null)

    private val _isLoading = mutableStateOf(false)
    val isLoading: Boolean
        get() = _isLoading.value

    suspend fun submitAnd(
        onSuccess: () -> Unit,
    ) {
        if (_isLoading.value) return
        try {
            _isLoading.value = true
            withContext(Dispatchers.IO) {
                val game = context.clientApi.startGameWithBot(startingPlayerColor)
                context.navigation.openGameWindow(game)
            }
            onSuccess()
        } finally {
            _isLoading.value = false
        }
    }
}
