package pl.edu.uj.tcs.rchess.model

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import pl.edu.uj.tcs.rchess.config.Config
import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString
import pl.edu.uj.tcs.rchess.server.ClientApi
import pl.edu.uj.tcs.rchess.server.Server
import java.io.File

class DatabaseTest {
    val config: Config = ConfigLoaderBuilder.default().addFileSource(File("config.yml")).build().loadConfigOrThrow()
    val clientApi: ClientApi = Server(config)

    @Test
    //TODO: make the running of this configurable in local.properties
    //TODO: make it check all the fens instead of just the last one
    //TODO: make it import stuff into the database? or just use the existing one like right now
    fun partialFenGenerationTest() = runTest {
        clientApi.getUserGames().forEach {
            if(it.finalPosition.toFenString(partial = true) !=
            it.finalGameState.currentState.toFenString(partial = true))
                println(it.getPlayerName(PlayerColor.WHITE))

            Assert.assertEquals(
                it.finalPosition.toFenString(partial = true),
                it.finalGameState.currentState.toFenString(partial = true)
            )
        }
    }
}