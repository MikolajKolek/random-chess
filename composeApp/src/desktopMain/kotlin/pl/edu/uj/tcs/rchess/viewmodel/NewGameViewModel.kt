package pl.edu.uj.tcs.rchess.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import pl.edu.uj.tcs.rchess.model.PlayerColor

class NewGameViewModel(private val context: AppContext): ViewModel() {
    /**
     * The color the user wants to play as.
     * [null] if the color should be picked randomly.
     */
    var startingPlayerColor by mutableStateOf<PlayerColor?>(null)
}
