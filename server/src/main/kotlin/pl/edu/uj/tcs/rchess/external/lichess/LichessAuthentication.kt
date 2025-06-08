package pl.edu.uj.tcs.rchess.external.lichess

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import pl.edu.uj.tcs.rchess.api.entity.AddExternalAccountResponse
import pl.edu.uj.tcs.rchess.api.entity.Service
import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.external.ExternalAuthentication
import pl.edu.uj.tcs.rchess.server.Database
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import javax.naming.TimeLimitExceededException
import kotlin.time.Duration.Companion.seconds

internal class LichessAuthentication(
    override val database: Database,
    override val userId: Int
) : ExternalAuthentication {
    val callback = CompletableDeferred<Unit>()

    // Setting port = 0 makes Ktor select a random available port
    val server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> =
    embeddedServer(Netty, host = authHost, port = 0) {
        routing {
            get("/") {
                val code = call.parameters["code"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                call.parameters["state"].takeIf {
                    it == expectedState
                } ?: return@get call.respond(HttpStatusCode.BadRequest)

                database.databaseScope.launch {
                    try {
                        val accessToken = fetchAccessToken(code)
                        val serviceAccount = fetchUserInfo(accessToken)
                        database.insertServiceAccount(serviceAccount, accessToken, userId)

                        callback.complete(Unit)
                    } catch (e: Throwable) {
                        callback.completeExceptionally(e)
                    }
                }

                call.respondHtml {
                    body {
                        h1 {
                            +"Success, you may close this page"
                        }
                    }
                }

                application.launch {
                    server.stop(1000, 1000)
                }
            }
        }
    }.start(wait = false)

    val httpClient = HttpClient(CIO)
    val port = runBlocking {
        server.engine.resolvedConnectors().first().port
    }

    val expectedState = generateRandomState()
    val codeVerifier = generateRandomCodeVerifier()
    val redirectUri = "http://localhost:$port/"
    val codeChallenge = generateCodeChallenge(codeVerifier)

    override suspend fun authenticate(): AddExternalAccountResponse {
        database.databaseScope.launch {
            delay(60.seconds)

            if(!callback.isCompleted) {
                server.stop(0, 0)
                callback.completeExceptionally(
                    TimeLimitExceededException(
                        "You ran out of time to authenticate with Lichess."
                    )
                )
            }
        }

        return AddExternalAccountResponse(
            oAuthUrl = createAuthUrl(),
            completionCallback = callback
        )
    }

    private fun createAuthUrl(): String {
        return "$lichessUrl/oauth?${
            listOf(
                "response_type" to "code",
                "client_id" to lichessClientId,
                "redirect_uri" to redirectUri,
                "scope" to lichessScope,
                "state" to expectedState,
                "code_challenge" to codeChallenge,
                "code_challenge_method" to "S256"
            ).formUrlEncode()
        }"
    }

    private suspend fun fetchAccessToken(code: String): String {
        val responseString = httpClient.submitForm(
            url = "$lichessApiUrl/token",

            formParameters = Parameters.build {
                append("grant_type", "authorization_code")
                append("code", code)
                append("redirect_uri", redirectUri)
                append("client_id", lichessClientId)
                append("code_verifier", codeVerifier)
            }
        )
        val response = Json.decodeFromString<JsonObject>(responseString.body())

        return response["access_token"]?.jsonPrimitive?.content
            ?: throw Exception("Failed to get access token")
    }

    private suspend fun fetchUserInfo(token: String): ServiceAccount {
        val responseString = httpClient.get("$lichessApiUrl/account") {
            header("Authorization", "Bearer $token")
        }
        val response = Json.decodeFromString<JsonObject>(responseString.body())

        return ServiceAccount(
            service = Service.LICHESS,
            userIdInService = response["id"]!!.jsonPrimitive.content,
            displayName = response["username"]!!.jsonPrimitive.content,
            isBot = false,
            isCurrentUser = true
        )
    }

    private fun generateCodeChallenge(codeVerifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(codeVerifier.toByteArray(Charsets.US_ASCII))

        return encodeToString(digest)
    }

    private fun generateRandomCodeVerifier(): String =
        encodeToString(generateSecureBytes(32))

    private fun generateRandomState(): String =
        encodeToString(generateSecureBytes(16))

    private fun encodeToString(bytes: ByteArray): String =
        Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)

    private fun generateSecureBytes(length: Int): ByteArray =
        ByteArray(length).apply { SecureRandom().nextBytes(this) }
}