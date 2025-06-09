package pl.edu.uj.tcs.rchess.view.board

import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.pieces.*
import rchess.composeapp.generated.resources.*

val Piece.icon
    get() = this.let { piece ->
        Res.drawable.run {
            when (piece.owner) {
                PlayerColor.WHITE -> when (piece) {
                    is Pawn -> frecsa_wP
                    is Bishop -> frecsa_wB
                    is King -> frecsa_wK
                    is Knight -> frecsa_wN
                    is Queen -> frecsa_wQ
                    is Rook -> frecsa_wR
                    else -> throw RuntimeException("Tried to get icon for an invalid piece")
                }

                PlayerColor.BLACK -> when (piece) {
                    is Pawn -> frecsa_bP
                    is Bishop -> fresca_bB
                    is King -> frecsa_bK
                    is Knight -> frecsa_bN
                    is Queen -> frecsa_bQ
                    is Rook -> fresca_bR
                    else -> throw RuntimeException("Tried to get icon for an invalid piece")
                }
            }
        }
    }
