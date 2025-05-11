package pl.edu.uj.tcs.rchess.model.game

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import pl.edu.uj.tcs.rchess.model.state.GameState
import pl.edu.uj.tcs.rchess.model.state.GameStateChange
import pl.edu.uj.tcs.rchess.model.statemachine.Update

interface GameObserver {
    val updateFlow: SharedFlow<Update<GameState, GameStateChange>>

    val stateFlow: StateFlow<GameState>
}