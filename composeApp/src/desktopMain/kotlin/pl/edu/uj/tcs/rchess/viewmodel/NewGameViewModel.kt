package pl.edu.uj.tcs.rchess.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.server.BotOpponent
import pl.edu.uj.tcs.rchess.viewmodel.datastate.DataStateViewModel
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class NewGameViewModel(private val context: AppContext): ViewModel() {
    val opponentList = DataStateViewModel {
        context.clientApi.getBotOpponents()
    }

    var selectedOpponent by mutableStateOf<BotOpponent?>(null)

    /**
     * The color the user wants to play as.
     * null if the color should be picked randomly.
     */
    var startingPlayerColor by mutableStateOf<PlayerColor?>(null)

    var clockSettings: ClockSettings = ClockSettings(5.minutes, 3.seconds)

    /**
     * Indicates if all the form elements are set correctly and it's possible to submit
     */
    val readyToSubmit: Boolean
        get() = selectedOpponent != null

    private val _isLoading = mutableStateOf(false)
    val isLoading: Boolean
        get() = _isLoading.value

    suspend fun submitAnd(
        onSuccess: () -> Unit,
    ) {
        if (_isLoading.value) return
        val opponent = selectedOpponent ?: return

        try {
            _isLoading.value = true
            withContext(Dispatchers.IO) {
                val game = context.clientApi.startGameWithBot(
                    startingPlayerColor,
                    opponent,
                    clockSettings,
                )
                context.navigation.openGameWindow(game)
            }
            onSuccess()
        } finally {
            _isLoading.value = false
        }
    }
}
