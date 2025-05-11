package pl.edu.uj.tcs.rchess.model.state

import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.statemachine.Change

sealed class GameStateChange: Change<GameState> {
    /**
     * Apply a move to the current game state.
     */
    class MoveChange(
        val move: Move,

        /**
         * The change to [GameState.progress]
         */
        val progress: GameProgress,
    ) : GameStateChange() {
        override fun applyTo(state: GameState): GameState {
            require(state.progress !is GameProgress.Finished) {
                "Game is already over"
            }
            val boardState = state.currentState.applyMove(move)
            return state.copy(
                boardStates = state.boardStates + boardState,
                moves = state.moves + move,
                progress = progress,
            )
        }
    }

    /**
     * End the game, but not as a result of a move.
     *
     * For example, this change is emitted when the game ends due to a timeout or resignation.
     *
     * If the game ends as a result of a move, a [MoveChange] change is emitted instead.
     */
    class GameOverChange(
        val progress: GameProgress.Finished,
    ) : GameStateChange() {
        override fun applyTo(state: GameState): GameState {
            require(state.progress !is GameProgress.Finished) {
                "Game is already over"
            }
            return state.copy(
                progress = progress,
            )
        }
    }

    fun isGameOver() : Boolean = when (this) {
        is GameOverChange -> true
        is MoveChange -> progress is GameProgress.Finished
    }
}
