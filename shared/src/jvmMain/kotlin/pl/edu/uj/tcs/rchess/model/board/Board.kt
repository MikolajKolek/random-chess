package pl.edu.uj.tcs.rchess.model.board

import pl.edu.uj.tcs.rchess.model.Square
import pl.edu.uj.tcs.rchess.model.pieces.Piece

/**
 * A read-only interface for a chess board indexed by [Square].
 *
 * The [toBoard] and [toMutableBoard] methods are modeled after standard collections from [kotlin.collections]
 */
interface Board {
    operator fun get(square: Square): Piece?

    /**
     * Creates an immutable copy of this board
     */
    fun toBoard(): Board

    /**
     * Creates a mutable copy of the board
     */
    fun toMutableBoard(): MutableBoard

    /**
     * @return An iterator with all the not null pieces on the board.
     */
    // This method is only useful for counting pieces,
    // perhaps it should be replaced with a more specific function for that purpose
    fun notNullPieces(): Iterator<Piece>
}
