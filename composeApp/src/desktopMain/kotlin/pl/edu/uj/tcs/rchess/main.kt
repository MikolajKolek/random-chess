package pl.edu.uj.tcs.rchess

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Random Chess",
    ) {
        App()
    }
}