package pl.edu.uj.tcs.rchess.model.game

import kotlinx.coroutines.runBlocking
import pl.edu.uj.tcs.rchess.model.GameOverReason
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.state.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.state.ClockState
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import pl.edu.uj.tcs.rchess.model.state.GameState
import pl.edu.uj.tcs.rchess.model.state.GameStateChange
import pl.edu.uj.tcs.rchess.model.statemachine.StateMachine
import kotlin.time.Clock
import kotlin.time.Duration

class LiveGame(
    initialBoardState: BoardState = BoardState.initial,
    timeLimit: Duration = Duration.INFINITE,
) : GameObserver {
    val stateMachine: StateMachine<GameState, GameStateChange> =
        StateMachine(GameState.starting(initialBoardState, timeLimit))

    override val updateFlow
        get() = stateMachine.updateFlow
    override val stateFlow
        get() = stateMachine.stateFlow

    fun makeMove(move: Move, playerColor: PlayerColor) = runBlocking {
        stateMachine.withState { gameState ->
            require(gameState.progress is GameProgress.Running) { "The game is not running" }
            require(gameState.currentState.isLegalMove(move)) { "The move is not legal" }
            require(gameState.currentState.board[move.from]?.owner == playerColor) { "It's not your turn" }

            /*val nextBoardState = gameState.currentState.applyMove(move)
            nextBoardState.impliedGameOverReason()?.let {
                return@withState GameStateChange.MoveChange(move, GameProgress.Finished(
                    it,
                    GameResult.BLACK_WON//TODO:FIX,

                ))
            }*/

            GameStateChange.MoveChange(move, GameProgress.Running(
                ClockState.Running(
                    gameState.progress.otherPlayerClock.totalTime,
                    Clock.System.now() + gameState.progress.otherPlayerClock.remainingTime
                ),
                ClockState.Paused(
                    gameState.progress.currentPlayerClock.totalTime,
                    gameState.progress.currentPlayerClock.endsAt - Clock.System.now()
                )
            ))
        }
    }

    fun resign(playerColor: PlayerColor) = runBlocking {
        stateMachine.withState { gameState ->
            require(gameState.progress is GameProgress.Running) { "The game is not running" }

            val whitePlayerClock = gameState.getPlayerClock(PlayerColor.WHITE)
                ?: throw IllegalStateException("White player clock is null")
            val blackPlayerClock = gameState.getPlayerClock(PlayerColor.BLACK)
                ?: throw IllegalStateException("Black player clock is null")

            GameStateChange.GameOverChange(GameProgress.FinishedWithClockInfo(
                GameOverReason.RESIGNATION,
                GameResult.winFromPlayerColor(playerColor),
                ClockState.Paused(
                    whitePlayerClock.totalTime,
                    whitePlayerClock.remainingTime()
                ),
                ClockState.Paused(
                    blackPlayerClock.totalTime,
                    blackPlayerClock.remainingTime()
                )
            ))
        }
    }

    fun getGameInput(playerColor: PlayerColor): GameInput =
        LocalGameInput(playerColor)


    private inner class LocalGameInput(
        override val playerColor: PlayerColor,
    ) : GameInput {
        override fun makeMove(move: Move) {
            makeMove(move, playerColor)
        }

        override fun resign() {
            resign(playerColor)
        }
    }
}
