package pl.edu.uj.tcs.rchess

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import pl.edu.uj.tcs.rchess.server.ClientApi
import pl.edu.uj.tcs.rchess.server.Server
import java.io.File

val config: Config = ConfigLoaderBuilder.default().addFileSource(File("config.yml")).build().loadConfigOrThrow()
val clientApi: ClientApi = Server(config.database)

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Random Chess",
    ) {
        App(clientApi)
    }

//    Window(
//        onCloseRequest = ::exitApplication,
//        title = "Random Chess",
//    ) {
//        fun getTopRow(color: PlayerColor) = arrayOf(
//            Rook(color), Knight(color), Bishop(color), Queen(color),
//            King(color), Bishop(color), Knight(color), Rook(color),
//        )
//        fun getPawnRow(color: PlayerColor) = Array<Piece>(8) { Pawn(color) }
//
//        // TODO: Replace with proper data source, this is only temporary
//        val boardState = BoardState(
//            listOf(
//                *getTopRow(PlayerColor.WHITE),
//                *getPawnRow(PlayerColor.WHITE),
//                *Array(4 * 8) { null },
//                *getPawnRow(PlayerColor.BLACK),
//                *getTopRow(PlayerColor.BLACK),
//            ),
//            currentTurn = PlayerColor.WHITE,
//            enPassantTarget = null,
//            castlingRights = CastlingRights.full(),
//            halfmoveCounter = 0,
//            fullmoveNumber = 1
//        )
//        Board(boardState, PlayerColor.BLACK)
//    }
}
