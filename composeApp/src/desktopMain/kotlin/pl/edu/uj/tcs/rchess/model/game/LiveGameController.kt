package pl.edu.uj.tcs.rchess.model.game

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import pl.edu.uj.tcs.rchess.model.*
import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString
import pl.edu.uj.tcs.rchess.model.state.*
import pl.edu.uj.tcs.rchess.model.statemachine.StateMachine
import pl.edu.uj.tcs.rchess.server.Database
import pl.edu.uj.tcs.rchess.server.game.HistoryServiceGame
import pl.edu.uj.tcs.rchess.util.SingleTaskTimer
import kotlin.time.Duration

class LiveGameController(
    initialBoardState: BoardState = BoardState.initial,
    clockSettings: ClockSettings,
    val whitePlayerId: String,
    val blackPlayerId: String,
    private val database: Database
) : GameObserver {
    val stateMachine: StateMachine<GameState, GameStateChange> =
        StateMachine(GameState.starting(initialBoardState, clockSettings))
    private val previousPositions = mutableMapOf(initialBoardState.toFenString(true) to 1)

    override val updateFlow
        get() = stateMachine.updateFlow
    override val stateFlow
        get() = stateMachine.stateFlow
    override val finishedGame = CompletableDeferred<HistoryServiceGame>()

    val timer = SingleTaskTimer(Dispatchers.IO)
    init {
        val progress = stateFlow.value.progress

        if(progress is GameProgress.Running) runBlocking {
            setTimer(progress.currentPlayerClock.remainingTotalTime())
        }
    }

    suspend fun makeMove(move: Move, playerColor: PlayerColor) {
        withStateWrapper { gameState ->
            require(gameState.progress is GameProgress.Running) { "The game is not running" }
            require(gameState.currentState.isLegalMove(move)) { "The move is not legal" }
            require(gameState.currentState.board[move.from]?.owner == playerColor) { "It's not your turn" }

            val nextBoardState = gameState.currentState.applyMove(move)
            // Checks for game over states that BoardState detects
            nextBoardState.impliedGameOverReason()?.let {
                return@withStateWrapper GameStateChange.MoveChange(
                    move, GameProgress.FinishedWithClockInfo(
                        it,
                        getPlayerClock(gameState, PlayerColor.WHITE).toPausedAfterMove(),
                        getPlayerClock(gameState, PlayerColor.BLACK).toPausedAfterMove()
                    )
                )
            }

            // Checks for threefold repetition
            val fen = nextBoardState.toFenString(partial = true)
            previousPositions.getOrDefault(fen, 0).let {
                if(it < 2) {
                    previousPositions[fen] = it + 1
                    return@let
                }

                return@withStateWrapper GameStateChange.MoveChange(
                    move, GameProgress.FinishedWithClockInfo(
                        Draw(GameDrawReason.THREEFOLD_REPETITION),
                        getPlayerClock(gameState, PlayerColor.WHITE).toPausedWithoutMove(),
                        getPlayerClock(gameState, PlayerColor.BLACK).toPausedWithoutMove()
                    )
                )
            }

            val startedClock = gameState.progress.otherPlayerClock.toRunning()
            val pausedClock = gameState.progress.currentPlayerClock.toPausedAfterMove()

            setTimer(startedClock.remainingTotalTime())
            GameStateChange.MoveChange(
                move, GameProgress.Running(
                    startedClock,
                    pausedClock
                )
            )
        }
    }

    suspend fun immediateGameEnd(playerColor: PlayerColor, winReason: GameWinReason) {
        withStateWrapper { gameState ->
            require(gameState.progress is GameProgress.Running) { "The game is not running" }

            GameStateChange.GameOverChange(
                GameProgress.FinishedWithClockInfo(
                    Win(winReason, playerColor.opponent),
                    getPlayerClock(gameState, PlayerColor.WHITE).toPausedWithoutMove(),
                    getPlayerClock(gameState, PlayerColor.BLACK).toPausedWithoutMove()
                )
            )
        }
    }

    fun getGameInput(playerColor: PlayerColor): GameInput =
        LocalGameInput(playerColor)

    private fun getPlayerClock(state: GameState, color: PlayerColor): ClockState =
        state.getPlayerClock(color) ?: throw IllegalStateException("Player clock is null")

    private suspend fun setTimer(time: Duration) {
        timer.replaceTask(time) {
            withStateWrapper { state ->
                if(state.progress !is GameProgress.Running)
                    return@withStateWrapper null

                val opponent = state.currentState.currentTurn.opponent
                val result = if (state.currentState.hasAnyNonKingMaterial(opponent))
                    Win(GameWinReason.TIMEOUT, opponent)
                else
                    Draw(GameDrawReason.TIMEOUT_VS_INSUFFICIENT_MATERIAL)

                GameStateChange.GameOverChange(
                    GameProgress.FinishedWithClockInfo(
                        result,
                        getPlayerClock(state, PlayerColor.WHITE).toPausedWithoutMove(),
                        getPlayerClock(state, PlayerColor.BLACK).toPausedWithoutMove()
                    )
                )
            }
        }
    }

    private suspend fun withStateWrapper(block: suspend (state: GameState) -> GameStateChange?): GameState {
        val updatedState = stateMachine.withState(block)

        if(updatedState.progress is GameProgress.FinishedWithClockInfo) {
            timer.stop()
            //TODO: this should spawn in an unconnected coroutine somewhere in the db scope
            finishedGame.complete(database.saveGame(updatedState, blackPlayerId, whitePlayerId))
        }

        return updatedState
    }

    private inner class LocalGameInput(
        override val playerColor: PlayerColor,
    ) : GameInput {
        override suspend fun makeMove(move: Move) {
            makeMove(move, playerColor)
        }

        override suspend fun resign() {
            immediateGameEnd(playerColor, GameWinReason.RESIGNATION)
        }

        override suspend fun abandon() {
            immediateGameEnd(playerColor, GameWinReason.ABANDONMENT)
        }
    }
}
