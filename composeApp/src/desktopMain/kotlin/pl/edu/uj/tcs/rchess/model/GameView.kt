package pl.edu.uj.tcs.rchess.model

import kotlin.reflect.KClass

/**
 * Interface for getting all the states of a game.
 *
 * It should record the entire history of the game and provide
 * all the information required to determine possible moves.
 *
 * `GameView` cannot be used to modify the game state.
 */
interface GameView {
    data class StatePiece(
        val kind: KClass<Piece>,
        val color: PlayerColor,
    )

    data class Move(
        val from: SquarePosition,
        val to: SquarePosition,
        val promoteTo: KClass<Piece>? = null,
    )

    interface State {
        /**
         * @return null if there is no piece at the position
         */
        fun getPieceAt(position: SquarePosition): StatePiece?

        /**
         * Color of the next player to move
         */
        val currentTurn: PlayerColor

        val enPassantSquare: SquarePosition?

        val castlingRights: CastlingRights

        /**
         * @return List of legal moves for the piece at the given position.
         * If there is no piece at the position, return an empty list.
         */
        fun getPossibleMovesFor(position: SquarePosition): List<Move>
    }

    val states: List<State>

    val initialState: State
        get() = states.first()

    /**
     * @return The move from state `i` to `i+1`
     */
    fun getMove(i: Int): Move
}

