package pl.edu.uj.tcs.rchess.model.view

import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.GameOverReason
import pl.edu.uj.tcs.rchess.model.Move

abstract class Change {
    /**
     * Emitted when a move is made and a new board state is created.
     */
    class MoveChange(
        val move: Move,

        val boardState: BoardState,

        /**
         * Reason for game over if the move caused the game to end.
         */
        val gameOverReason: GameOverReason?,
    ) : Change()

    /**
     * Emitted when a previously stored move is undone.
     */
    class RevertChange(
        /**
         * A list of undone moves in reverse order (from newest to oldest).
         */
        val droppedMoves: List<Move>,
    ) : Change()

    /**
     * Emitted when the game ends, but not as a result of a move.
     * If the game ends as a result of a move, a [MoveChange] even is emitted instead.
     */
    class GameOverChange(
        val gameOverReason: GameOverReason,
    ) : Change()
}
