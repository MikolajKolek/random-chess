package pl.edu.uj.tcs.rchess.server

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.time.Duration

class LichessConnectionTest {
    internal val server = Server()

    @Test
    fun testConnection() {
        runBlocking {
            server.requestResync()
            delay(Duration.INFINITE)
        }
    }
}