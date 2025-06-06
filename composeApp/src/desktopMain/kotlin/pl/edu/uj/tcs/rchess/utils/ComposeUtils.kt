package pl.edu.uj.tcs.rchess.utils

import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.first

suspend inline fun waitUntil(crossinline condition: () -> Boolean) {
    snapshotFlow { condition() }.first { it }
}
