package pl.edu.uj.tcs.rchess.proxy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import pl.edu.uj.tcs.rchess.api.ClientApi
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong
import kotlin.time.Duration.Companion.milliseconds

/**
 * A demo implementation of a network layer for [ClientApi].
 * This class simulates a poor network connection with extra delays and occasional errors.
 */
class DemoRemoteProxy(
    server: ClientApi,
    initiallyEnabled: Boolean,
) : AbstractProxy(server) {
    var enabled by mutableStateOf(initiallyEnabled)

    override suspend fun <T> proxy(action: suspend () -> T): T {
        if (enabled) {
            delay(Random.nextLong(150L..600L).milliseconds)
            if (Random.nextInt(0..<100) < 25) {
                throw Exception("Demo network failure")
            }
        }
        return action()
    }
}
