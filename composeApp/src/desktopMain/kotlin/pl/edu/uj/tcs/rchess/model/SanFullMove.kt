package pl.edu.uj.tcs.rchess.model

/**
 * Full move (a white and a black move) in SAN notation.
 *
 * Implemented as a sealed class to represent the fact
 * that at most one of the half-moves can be null.
 */
sealed interface SanFullMove {
    val number: Int
    val white: HalfMove?
    val black: HalfMove?

    class HalfMove(
        /**
         * The position of this move in the [GameState.moves] list
         */
        val moveIndex: Int,
        val san: String,
    )

    class InitialBlackMove(
        override val number: Int,
        override val black: HalfMove,
    ): SanFullMove {
        override val white: Nothing? = null
    }

    class WithWhiteMove(
        override val number: Int,
        override val white: HalfMove,
        override val black: HalfMove?,
    ) : SanFullMove
}
