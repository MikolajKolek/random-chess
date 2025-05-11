package pl.edu.uj.tcs.rchess.model.board

import pl.edu.uj.tcs.rchess.model.Square
import pl.edu.uj.tcs.rchess.model.pieces.Piece

/**
 * A mutable interface for a chess board indexed by [Square].
 */
class MutableBoard private constructor(
    private val backing: MutableList<Piece?>
): Board {
    init {
        require(backing.size == 64) { "Board must have 64 squares." }
    }

    override fun get(square: Square): Piece? = backing[square.positionInBoard()]

    operator fun set(square: Square, piece: Piece?) {
        backing[square.positionInBoard()] = piece
    }

    override fun toBoard(): Board {
        // The mutable board copy is exposed as a readonly interface,
        // so external modifications are not possible without a cast
        return toMutableBoard()
    }

    override fun toMutableBoard(): MutableBoard =
        MutableBoard(backing.toMutableList())

    override fun notNullPieces(): Iterator<Piece> = backing.asSequence().filterNotNull().iterator()

    companion object {
        fun empty() = MutableBoard(MutableList(64) { null })

        private fun Square.positionInBoard() = (8 * rank) + file
    }
}

// Same logic as in [MutableBoard.toBoard()]
fun emptyBoard(): Board = MutableBoard.empty()
