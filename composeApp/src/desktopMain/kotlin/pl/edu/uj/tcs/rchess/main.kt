package pl.edu.uj.tcs.rchess

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import pl.edu.uj.tcs.rchess.components.Board

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Random Chess",
    ) {
        App()
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Random Chess",
    ) {
        Board()
    }
}
