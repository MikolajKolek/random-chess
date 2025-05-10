package pl.edu.uj.tcs.rchess.model.observer

import kotlinx.coroutines.flow.SharedFlow
import pl.edu.uj.tcs.rchess.model.state.GameStateChange
import pl.edu.uj.tcs.rchess.model.state.ImmutableGameState
import pl.edu.uj.tcs.rchess.model.statemachine.Update

interface GameObserver {
    val messageFlow: SharedFlow<Update<ImmutableGameState, GameStateChange>>
}
