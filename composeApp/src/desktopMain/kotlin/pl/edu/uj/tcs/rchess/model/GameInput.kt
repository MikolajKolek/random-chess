package pl.edu.uj.tcs.rchess.model

interface GameInput {
    /**
     * Makes a move on the chessboard
     * @throws IllegalArgumentException if the argument is not a valid move, or it is not the moving player's turn
     */
    fun makeMove(move: Move)

    /**
     * Resigns the game
     */
    fun resign()

    /**
     * The color of the player associated with the GameInput instance
     */
    fun getColor() : PlayerColor
}