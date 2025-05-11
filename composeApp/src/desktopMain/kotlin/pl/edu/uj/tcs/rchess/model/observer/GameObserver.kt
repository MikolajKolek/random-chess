package pl.edu.uj.tcs.rchess.model.observer

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import pl.edu.uj.tcs.rchess.model.state.GameState
import pl.edu.uj.tcs.rchess.model.state.GameStateChange
import pl.edu.uj.tcs.rchess.model.statemachine.Update

interface GameObserver {
    val messageFlow: SharedFlow<Update<GameState, GameStateChange>>

    val stateFlow: StateFlow<GameState>
}
