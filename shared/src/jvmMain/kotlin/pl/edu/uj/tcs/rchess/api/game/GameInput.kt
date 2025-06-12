package pl.edu.uj.tcs.rchess.api.game

import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor

/**
 * An input used to interact with a chess game.
 */
interface GameInput {
    /**
     * The color of the player associated with the GameInput instance.
     */
    val playerColor: PlayerColor

    /**
     * Makes a move on the chessboard.
     * @throws IllegalArgumentException if the argument is
     * not a valid move, or it is not the moving player's turn.
     */
    suspend fun makeMove(move: Move)

    /**
     * Resigns the game.
     *
     * Resignation is voluntary and only happens because the player chooses to do so.
     * @see abandon
     */
    suspend fun resign()

    /**
     * Abandons the game.
     *
     * Abandonment can, for example, be used when the player disconnects from the server
     * or when the bot crashes. This means that it's not usually voluntary, even if the player
     * may be able to choose to abandon the game, for example, by intentionally disconnecting.
     * @see resign
     */
    suspend fun abandon()
}
