package pl.edu.uj.tcs.rchess.api.game

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryServiceGame
import pl.edu.uj.tcs.rchess.model.state.GameState
import pl.edu.uj.tcs.rchess.model.state.GameStateChange
import pl.edu.uj.tcs.rchess.model.statemachine.Update

/**
 * An object used to observe a game being played live.
 */
interface GameObserver {
    /**
     * A flow emitting updates to the [GameState].
     */
    val updateFlow: SharedFlow<Update<GameState, GameStateChange>>

    /**
     * When the game finishes, it is saved to the database, and this field is set to it's corresponding HistoryGame
     */
    val finishedGame: Deferred<HistoryServiceGame>

    /**
     * A [StateFlow] containing the current state of the game.
     */
    val stateFlow: StateFlow<GameState>
}
