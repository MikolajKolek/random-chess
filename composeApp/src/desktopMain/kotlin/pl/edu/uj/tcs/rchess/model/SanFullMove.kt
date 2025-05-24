package pl.edu.uj.tcs.rchess.model

/**
 * Full move (a white and a black move) in SAN notation.
 *
 * Implemented as a sealed class to represent the fact
 * that at most one of the half-moves can be null.
 */
sealed class SanFullMove(
    val number: Int,
    val white: HalfMove?,
    val black: HalfMove?,
) {
    init {
        require(white != null || black != null)
    }

    class HalfMove(
        /**
         * The position of this move in the [GameState.moves] list
         */
        val moveIndex: Int,
        val san: String,
    )

    class InitialBlackMove(
        number: Int,
        black: HalfMove
    ): SanFullMove(number,null, black)

    class FullMove(
        number: Int,
        white: HalfMove,
        black: HalfMove,
    ) : SanFullMove(number, white, black)

    class FinalWhiteMove(
        number: Int,
        white: HalfMove,
    ) : SanFullMove(number, white, null)
}
