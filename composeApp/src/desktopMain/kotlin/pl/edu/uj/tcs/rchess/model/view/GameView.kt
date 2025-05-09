package pl.edu.uj.tcs.rchess.model.view

import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move

interface GameView {
    val boardStates: List<BoardState>

    /**
     * @return State of the board before the first move
     */
    val initialState: BoardState
        get() = boardStates.first()

    /**
     * @return The move to go from position i to i+1
     */
    fun getMove(i: Int): Move

    /**
     * Getter for the white player clock state
     */
    val whiteClockState: ClockState

    /**
     * Getter for the black player clock state
     */
    val blackClockState: ClockState

    /**
     * Add a listener which will be called after the game state changes
     */
    fun addChangeListener(listener: (Change) -> Unit)

    /**
     * Remove a game state change listener which was added before
     */
    fun removeChangeListener(listener: (Change) -> Unit)
}
