package pl.edu.uj.tcs.rchess.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.PlayerColor
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

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
                val botOpponents = context.clientApi.getBotOpponents()
                val game = context.clientApi.startGameWithBot(
                    startingPlayerColor,
                    // TODO: Use selected opponent and clock settings
                    botOpponents[0],
                    ClockSettings(5.minutes, 3.seconds, 5.seconds),
                )
                context.navigation.openGameWindow(game)
            }
            onSuccess()
        } finally {
            _isLoading.value = false
        }
    }
}
