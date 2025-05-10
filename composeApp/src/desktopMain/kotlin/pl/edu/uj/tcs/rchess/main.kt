package pl.edu.uj.tcs.rchess

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import kotlinx.coroutines.runBlocking
import pl.edu.uj.tcs.rchess.components.GameScreen
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.GameOverReason
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.state.ClockState
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import pl.edu.uj.tcs.rchess.model.state.GameStateChange
import pl.edu.uj.tcs.rchess.model.state.ImmutableGameState
import pl.edu.uj.tcs.rchess.model.statemachine.StateMachine
import pl.edu.uj.tcs.rchess.server.ClientApi
import pl.edu.uj.tcs.rchess.server.Server
import java.io.File
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

val config: Config = ConfigLoaderBuilder.default().addFileSource(File("config.yml")).build().loadConfigOrThrow()
val clientApi: ClientApi = Server(config.database)

@OptIn(ExperimentalTime::class)
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Random Chess",
    ) {
        App(clientApi)
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Random Chess",
    ) {
        val gameState = remember {
            val gameStateMachine = StateMachine(ImmutableGameState(
                boardStates = listOf(BoardState.initial()),
                moves = emptyList(),
                progress = GameProgress.Running(
                    currentPlayerClock = ClockState.Running(totalTime = 10.seconds, endsAt = Clock.System.now() + 10.seconds),
                    otherPlayerClock = ClockState.Paused(totalTime = 10.seconds, remainingTime = 5.seconds),
                ),
            ))

            runBlocking {
//                gameStateMachine.withState { state ->
//                    GameStateChange.MoveChange(
//                        move = Move(Square(1, 0), Square(2, 0), null),
//                        progress = state.progress,
//                    )
//                }
//
//                gameStateMachine.withState { state ->
//                    GameStateChange.MoveChange(
//                        move = Move(Square(7, 0), Square(5, 0), null),
//                        progress = state.progress,
//                    )
//                }

                return@runBlocking gameStateMachine.withState {
                    GameStateChange.GameOverChange(
                        progress = GameProgress.Finished(
                            whitePlayerClock = ClockState.Paused(totalTime = 10.seconds, remainingTime = 0.seconds),
                            blackPlayerClock = ClockState.Paused(totalTime = 10.seconds, remainingTime = 5.seconds),
                            reason = GameOverReason.TIMEOUT,
                            result = GameResult.BLACK_WON,
                        ),
                    )
                }
            }
        }


        GameScreen(gameState, null)
    }
}
