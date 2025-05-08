package pl.edu.uj.tcs.rchess.model

/**
 * Square represents one of the 64 playable squares of the chessboard.
 */
class Square(
    val position: SquarePosition,
    var piece: Piece? = null,
)
