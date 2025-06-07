package pl.edu.uj.tcs.rchess.external

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.server.Database
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

internal class ChessComConnection(
    override val database: Database,
    override val serviceAccount: ServiceAccount
) : ExternalConnection {
    val httpClient = HttpClient(CIO)
    var lastRequest: Instant? = null

    override fun available(): Boolean {
        val currentLastRequest = lastRequest
        return (currentLastRequest == null || Clock.System.now() - currentLastRequest >= 5.seconds)
    }

    override suspend fun synchronize(): Boolean {
        if(!available())
            return true

        lastRequest = Clock.System.now()

        val latestGame = database.getLatestGameForServiceAccount(serviceAccount)

        return true
    }
}