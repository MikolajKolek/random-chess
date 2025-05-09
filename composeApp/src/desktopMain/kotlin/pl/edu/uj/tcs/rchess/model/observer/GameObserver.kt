@file:Suppress("KDocUnresolvedReference")

package pl.edu.uj.tcs.rchess.model.observer

import kotlinx.coroutines.channels.SendChannel
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move

interface GameObserver {
    val boardStates: List<BoardState>

    /**
     * `moves[i]` represents the move from `boardStates[i]` to `boardStates[i + 1]`
     */
    val moves: List<Move>

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

    /**
     * Getter for the white player clock state
     */
    val whiteClockState: ClockState

    /**
     * Getter for the black player clock state
     */
    val blackClockState: ClockState

    /**
     * Add a channel which will be sent a [Change] object after the game state changes
     */
    fun addChangeChannel(channel: SendChannel<Change>)

    /**
     * Remove a game state change channel which was added before
     */
    fun removeChangeChanel(channel: SendChannel<Change>)
}
