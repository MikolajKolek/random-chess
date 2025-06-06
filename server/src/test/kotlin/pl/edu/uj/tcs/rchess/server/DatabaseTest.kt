package pl.edu.uj.tcs.rchess.server

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString

class DatabaseTest {
    val clientApi: ClientApi = Server()

    @Test
    //TODO: make the running of this configurable in local.properties
    //TODO: make it check all the fens instead of just the last one
    //TODO: make it import stuff into the database? or just use the existing one like right now
    fun partialFenGenerationTest() = runBlocking {
        clientApi.getUserGames(ClientApi.GamesRequestSettings(length = Int.MAX_VALUE)).forEach {
            if(it.finalPosition.toFenString(partial = true) !=
            it.finalGameState.currentState.toFenString(partial = true))
                println(it.whitePlayer.displayName)

            Assert.assertEquals(
                it.finalPosition.toFenString(partial = true),
                it.finalGameState.currentState.toFenString(partial = true)
            )
        }
    }
}