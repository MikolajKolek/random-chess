package pl.edu.uj.tcs.rchess.model

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import pl.edu.uj.tcs.rchess.model.Fen.Companion.fromFen
import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString
import pl.edu.uj.tcs.rchess.model.game.LiveGameController
import pl.edu.uj.tcs.rchess.model.state.BoardState
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

val infiniteClock = ClockSettings(
    startingTime = Duration.INFINITE,
    moveIncrease = 0.seconds,
    extraTimeForFirstMove = 0.seconds
)

class LiveGameTest {
    class Wrapper(
        initialBoardState: String = BoardState.initial.toFenString(),
        clockSettings: ClockSettings = infiniteClock
    ) {
        val game = LiveGameController(BoardState.fromFen(initialBoardState), clockSettings)
        val whiteInput = game.getGameInput(PlayerColor.WHITE)
        val blackInput = game.getGameInput(PlayerColor.BLACK)

        fun move(move: Move) : Wrapper {
            Assert.assertTrue(game.stateFlow.value.progress is GameProgress.Running)

            runBlocking {
                when (game.stateFlow.value.currentState.currentTurn) {
                    PlayerColor.WHITE -> whiteInput.makeMove(move)
                    PlayerColor.BLACK -> blackInput.makeMove(move)
                }
            }

            return this
        }

        fun resign(): Wrapper {
            runBlocking {
                when (game.stateFlow.value.currentState.currentTurn) {
                    PlayerColor.WHITE -> whiteInput.resign()
                    PlayerColor.BLACK -> blackInput.resign()
                }
            }

            return this
        }

        fun move(move: String): Wrapper = move(Move.fromLongAlgebraicNotation(move))

        fun getResult(): GameResult {
            Assert.assertTrue(game.stateFlow.value.progress is GameProgress.FinishedWithClockInfo)
            return (game.stateFlow.value.progress as GameProgress.FinishedWithClockInfo).result
        }
    }

    fun assertDrawReason(expected: GameDrawReason, actual: GameResult) {
        Assert.assertTrue(actual is Draw)
        Assert.assertEquals(expected, (actual as Draw).drawReason)
    }

    fun assertWinReason(expected: GameWinReason, actual: GameResult) {
        Assert.assertTrue(actual is Win)
        Assert.assertEquals(expected, (actual as Win).winReason)
    }

    @Test
    fun threefoldRepetitionTest() {
        val game = Wrapper("k7/8/8/8/8/8/7P/K7 w - - 0 1")
            .move("a1a2").move("a8a7")
            .move("a2a1").move("a7a8")
            .move("a1a2").move("a8a7")
            .move("a2a1").move("a7a8")

        assertDrawReason(GameDrawReason.THREEFOLD_REPETITION, game.getResult())
    }

    @Test
    fun insufficientMaterialTest() {
        val game = Wrapper("k7/1N6/8/8/8/8/1n6/K7 w - - 0 1")
            .move("a1b2")

        assertDrawReason(GameDrawReason.INSUFFICIENT_MATERIAL, game.getResult())
    }

    @Test
    fun fiftyMoveRuleTest() {
        val game = Wrapper("r3k2r/8/8/8/8/2N2N2/1B3QB1/R3K2R w KQkq - 99 1")
            .move("e1e2")

        assertDrawReason(GameDrawReason.FIFTY_MOVE_RULE, game.getResult())
    }

    @Test
    fun stalemateTest() {
        val game = Wrapper("K4Q2/8/8/8/8/8/8/7k w - - 0 1")
            .move("f8f2")

        assertDrawReason(GameDrawReason.STALEMATE, game.getResult())
    }

    @Test
    fun resignationTest() {
        val game = Wrapper().resign()

        Assert.assertEquals(Win(GameWinReason.RESIGNATION, PlayerColor.BLACK), game.getResult())
    }

    //TODO:timeout test
    // checkmate test
    // TIMEOUT_VS_INSUFFICIENT_MATERIAL,
}
