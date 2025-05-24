package pl.edu.uj.tcs.rchess.model.game

import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor

interface GameInput {
    /**
     * The color of the player associated with the GameInput instance
     */
    val playerColor: PlayerColor

    /**
     * Makes a move on the chessboard
     * @throws IllegalArgumentException if the argument is not a valid move, or it is not the moving player's turn
     */
    suspend fun makeMove(move: Move)

    /**
     * Resigns the game
     */
    fun resign()
}
