package pl.edu.uj.tcs.rchess.model.state

import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import kotlin.time.Clock
import kotlin.time.Duration

/**
 * Immutable class holding the full state of the game including history and clocks
 */
data class GameState(
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
            is GameProgress.FinishedWithClockInfo -> progress.playerClock(color)
            is GameProgress.Finished -> null
            is GameProgress.Running -> {
                if (color == currentState.currentTurn) progress.currentPlayerClock
                else progress.otherPlayerClock
            }
        }

    companion object {
        fun starting(initialBoardState: BoardState, timeLimit: Duration) = GameState(
            boardStates = listOf(initialBoardState),
            moves = emptyList(),
            progress = GameProgress.Running(
                currentPlayerClock = ClockState.Running(timeLimit, Clock.System.now() + timeLimit),
                otherPlayerClock = ClockState.Paused(timeLimit, timeLimit)
            ),
        )

        /**
         * Creates an already finished game state by recreating [boardStates] just from [moves]
         */
        fun finished(
            initialBoardState: BoardState,
            moves: List<Move>,
            finishedProgress: GameProgress.Finished,
        ): GameState {
            val boardStates = moves.runningFold(initialBoardState) { boardState, move ->
                boardState.applyMove(move)
            }
            return GameState(
                boardStates = boardStates,
                moves = moves,
                progress = finishedProgress,
            )
        }
    }
}
