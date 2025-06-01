package pl.edu.uj.tcs.rchess.model.pieces

import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square

class Bishop(owner: PlayerColor): StraightLinePiece(owner = owner) {
    override val vectors = listOf(
        Square.Vector(1, 1),
        Square.Vector(1, -1),
        Square.Vector(-1, 1),
        Square.Vector(-1, -1)
    )

    override val fenLetterLowercase = 'b'

    override val unicodeSymbol = when (owner) {
        PlayerColor.WHITE -> "♗"
        PlayerColor.BLACK -> "♝"
    }
}
