package pl.edu.uj.tcs.rchess.view.board

import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.pieces.Bishop
import pl.edu.uj.tcs.rchess.model.pieces.King
import pl.edu.uj.tcs.rchess.model.pieces.Knight
import pl.edu.uj.tcs.rchess.model.pieces.Pawn
import pl.edu.uj.tcs.rchess.model.pieces.Piece
import pl.edu.uj.tcs.rchess.model.pieces.Queen
import pl.edu.uj.tcs.rchess.model.pieces.Rook
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.frecsa_bK
import rchess.composeapp.generated.resources.frecsa_bN
import rchess.composeapp.generated.resources.frecsa_bP
import rchess.composeapp.generated.resources.frecsa_bQ
import rchess.composeapp.generated.resources.frecsa_wB
import rchess.composeapp.generated.resources.frecsa_wK
import rchess.composeapp.generated.resources.frecsa_wN
import rchess.composeapp.generated.resources.frecsa_wP
import rchess.composeapp.generated.resources.frecsa_wQ
import rchess.composeapp.generated.resources.frecsa_wR
import rchess.composeapp.generated.resources.fresca_bB
import rchess.composeapp.generated.resources.fresca_bR

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
