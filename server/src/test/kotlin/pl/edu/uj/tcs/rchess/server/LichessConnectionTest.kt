package pl.edu.uj.tcs.rchess.server

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import pl.edu.uj.tcs.rchess.api.entity.Service
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

    @Test
    fun testAuthentication() {
        runBlocking {
            println(server.serviceAccounts.value)

            val response = server.addExternalAccount(Service.LICHESS)
            println(response.oauthURL)
            response.completionCallback.await()

            println(server.serviceAccounts.value)
        }
    }
}