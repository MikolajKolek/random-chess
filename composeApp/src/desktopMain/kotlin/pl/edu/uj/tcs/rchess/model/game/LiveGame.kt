package pl.edu.uj.tcs.rchess.model.game

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import pl.edu.uj.tcs.rchess.model.*
import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString
import pl.edu.uj.tcs.rchess.model.state.*
import pl.edu.uj.tcs.rchess.model.statemachine.StateMachine
import pl.edu.uj.tcs.rchess.server.ServiceGame
import pl.edu.uj.tcs.rchess.util.SingleTaskTimer
import kotlin.time.Duration

class LiveGame(
    initialBoardState: BoardState = BoardState.initial,
    clockSettings: ClockSettings,
) : GameObserver {
    val stateMachine: StateMachine<GameState, GameStateChange> =
        StateMachine(GameState.starting(initialBoardState, clockSettings))
    private val previousPositions = mutableMapOf(initialBoardState.toFenString(true) to 1)

    override val updateFlow
        get() = stateMachine.updateFlow
    override val stateFlow
        get() = stateMachine.stateFlow
    override val finishedGame = CompletableDeferred<ServiceGame>()

    val timer = SingleTaskTimer(Dispatchers.IO)
    init {
        val progress = stateFlow.value.progress

        if(progress is GameProgress.Running) runBlocking {
            setTimer(progress.currentPlayerClock.remainingTotalTime())
        }
    }

    suspend fun makeMove(move: Move, playerColor: PlayerColor) {
        stateMachine.withState { gameState ->
            require(gameState.progress is GameProgress.Running) { "The game is not running" }
            require(gameState.currentState.isLegalMove(move)) { "The move is not legal" }
            require(gameState.currentState.board[move.from]?.owner == playerColor) { "It's not your turn" }

            val nextBoardState = gameState.currentState.applyMove(move)
            nextBoardState.impliedGameOverReason()?.let {
                endGame(gameState)

                return@withState GameStateChange.MoveChange(
                    move, GameProgress.FinishedWithClockInfo(
                        it,
                        getPlayerClock(gameState, PlayerColor.WHITE).toPausedAfterMove(),
                        getPlayerClock(gameState, PlayerColor.BLACK).toPausedAfterMove()
                    )
                )
            }

            val fen = nextBoardState.toFenString(partial = true)
            previousPositions.getOrDefault(fen, 0).let {
                if(it < 2) {
                    previousPositions[fen] = it + 1
                    return@let
                }

                return@withState GameStateChange.GameOverChange(
                    GameProgress.FinishedWithClockInfo(
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

    suspend fun resign(playerColor: PlayerColor) {
        stateMachine.withState { gameState ->
            require(gameState.progress is GameProgress.Running) { "The game is not running" }

            endGame(gameState)
            GameStateChange.GameOverChange(
                GameProgress.FinishedWithClockInfo(
                    Win(GameWinReason.RESIGNATION, playerColor.opponent),
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
            stateMachine.withState { state ->
                if(state.progress !is GameProgress.Running)
                    return@withState null

                val opponent = state.currentState.currentTurn.opponent
                val result = if (state.currentState.hasAnyNonKingMaterial(opponent))
                    Win(GameWinReason.TIMEOUT, opponent)
                else
                    Draw(GameDrawReason.TIMEOUT_VS_INSUFFICIENT_MATERIAL)

                endGame(state)
                return@withState GameStateChange.GameOverChange(
                    GameProgress.FinishedWithClockInfo(
                        result,
                        getPlayerClock(state, PlayerColor.WHITE).toPausedWithoutMove(),
                        getPlayerClock(state, PlayerColor.BLACK).toPausedWithoutMove()
                    )
                )
            }
        }
    }

    //TODO: implement game saving after it's done
    private fun endGame(state: GameState) {
        timer.stop()
    }

    private inner class LocalGameInput(
        override val playerColor: PlayerColor,
    ) : GameInput {
        override suspend fun makeMove(move: Move) {
            makeMove(move, playerColor)
        }

        override suspend fun resign() {
            resign(playerColor)
        }
    }
}
