package pl.edu.uj.tcs.rchess.model.state

import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.SanFullMove

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

    /**
     * List of full moves in SAN notation.
     *
     * Only the first move can be a [SanFullMove.InitialBlackMove].
     * The last full move can have a null black move.
     */
    val fullMoves: List<SanFullMove> by lazy {
        buildList {
            var moveNumber = 1
            var whiteMove: SanFullMove.HalfMove? = null
            moves.zip(boardStates).forEachIndexed { index, (move, state) ->
                val halfMove = SanFullMove.HalfMove(index, state.moveToStandardAlgebraic(move))
                if (state.currentTurn == PlayerColor.WHITE) {
                    assert(whiteMove == null) { "The game state contains two consecutive white moves" }
                    whiteMove = halfMove
                } else if (whiteMove != null) {
                    add(SanFullMove.WithWhiteMove(moveNumber++, whiteMove, halfMove))
                    whiteMove = null
                } else {
                    assert(moveNumber == 1) { "The game state contains two consecutive black moves" }
                    add(SanFullMove.InitialBlackMove(moveNumber++, halfMove))
                }
            }
            if (whiteMove != null) add(SanFullMove.WithWhiteMove(moveNumber, whiteMove, null))
        }
    }

    companion object {
        fun starting(initialBoardState: BoardState, clockSettings: ClockSettings) = GameState(
            boardStates = listOf(initialBoardState),
            moves = emptyList(),
            progress = GameProgress.Running(
                currentPlayerClock = ClockState.RunningBeforeFirstMove(clockSettings),
                otherPlayerClock = ClockState.PausedBeforeFirstMove(clockSettings)
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
