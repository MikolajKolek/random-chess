package pl.edu.uj.tcs.rchess.model.state

import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor

/**
 * Immutable class holding the full state of the game including history and clocks
 */
data class ImmutableGameState(
    val boardStates: List<BoardState>,

    @Suppress("KDocUnresolvedReference")
    /**
     * `moves[i]` represents the move from `boardStates[i]` to `boardStates[i + 1]`
     */
    val moves: List<Move>,
    val progress: GameProgress,
) {
    init {
        require(boardStates.size == moves.size + 1) { "The boardState and move don't match" }
    }

    /**
     * @return State of the board before the first move
     */
    val initialState: BoardState
        get() = boardStates.first()

    /**
     * @return The current board state
     */
    val currentState: BoardState
        get() = boardStates.last()

    fun getPlayerClock(color: PlayerColor) =
        when (progress) {
            is GameProgress.Finished -> progress.playerClock(color)
            is GameProgress.Running -> {
                if (color == currentState.currentTurn) progress.currentPlayerTimeout
                else progress.otherPlayerRemainingTime
            }
        }
}
