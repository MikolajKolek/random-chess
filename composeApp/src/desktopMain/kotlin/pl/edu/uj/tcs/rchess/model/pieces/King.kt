package pl.edu.uj.tcs.rchess.model.pieces

import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

class King(owner: PlayerColor): VectorListPiece(owner = owner) {
    override val vectors = listOf(
        Square.Vector(1, 1),
        Square.Vector(1, 0),
        Square.Vector(1, -1),
        Square.Vector(0, 1),
        Square.Vector(0, -1),
        Square.Vector(-1, 1),
        Square.Vector(-1, 0),
        Square.Vector(-1, -1),
    )

    override val fenLetterLowercase = 'k'
}
