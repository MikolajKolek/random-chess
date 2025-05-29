package pl.edu.uj.tcs.rchess.model.game

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import pl.edu.uj.tcs.rchess.model.state.GameState
import pl.edu.uj.tcs.rchess.model.state.GameStateChange
import pl.edu.uj.tcs.rchess.model.statemachine.Update
import pl.edu.uj.tcs.rchess.server.game.HistoryServiceGame

interface GameObserver {
    val updateFlow: SharedFlow<Update<GameState, GameStateChange>>

    /**
     * When the game finishes, it is saved to the database, and this field is set to it's corresponding HistoryGame
     */
    val finishedGame: Deferred<HistoryServiceGame>

    val stateFlow: StateFlow<GameState>
}
