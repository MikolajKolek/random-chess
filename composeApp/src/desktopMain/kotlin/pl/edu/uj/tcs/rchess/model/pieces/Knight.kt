package pl.edu.uj.tcs.rchess.model.pieces

import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

class Knight(owner: PlayerColor): VectorListPiece(owner = owner) {
    override val vectors = listOf(
        Square.Vector(1, 2),
        Square.Vector(1, -2),
        Square.Vector(2, 1),
        Square.Vector(2, -1),
        Square.Vector(-1, 2),
        Square.Vector(-1, -2),
        Square.Vector(-2, 1),
        Square.Vector(-2, -1),
    )

    override val fenLetterLowercase = 'n'
}
