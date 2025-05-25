package pl.edu.uj.tcs.rchess.model.game

import kotlinx.coroutines.CompletableDeferred
import pl.edu.uj.tcs.rchess.model.*
import pl.edu.uj.tcs.rchess.model.state.*
import pl.edu.uj.tcs.rchess.model.statemachine.StateMachine
import pl.edu.uj.tcs.rchess.server.ServiceGame

class LiveGame(
    initialBoardState: BoardState = BoardState.initial,
    clockSettings: ClockSettings,
) : GameObserver {
    val stateMachine: StateMachine<GameState, GameStateChange> =
        StateMachine(GameState.starting(initialBoardState, clockSettings))

    override val updateFlow
        get() = stateMachine.updateFlow
    override val stateFlow
        get() = stateMachine.stateFlow
    override val finishedGame: CompletableDeferred<ServiceGame> = CompletableDeferred()

    suspend fun makeMove(move: Move, playerColor: PlayerColor) {
        stateMachine.withState { gameState ->
            require(gameState.progress is GameProgress.Running) { "The game is not running" }
            require(gameState.currentState.isLegalMove(move)) { "The move is not legal" }
            require(gameState.currentState.board[move.from]?.owner == playerColor) { "It's not your turn" }

            val nextBoardState = gameState.currentState.applyMove(move)
            nextBoardState.impliedGameOverReason()?.let {
                saveGame(gameState)

                return@withState GameStateChange.MoveChange(move, GameProgress.FinishedWithClockInfo(
                    it,
                    getPlayerClock(gameState, PlayerColor.WHITE).toPausedAfterMove(),
                    getPlayerClock(gameState, PlayerColor.BLACK).toPausedAfterMove()
                ))
            }

            GameStateChange.MoveChange(move, GameProgress.Running(
                gameState.progress.otherPlayerClock.toRunning(),
                gameState.progress.currentPlayerClock.toPausedAfterMove()
            ))
        }
    }

    suspend fun resign(playerColor: PlayerColor) {
        stateMachine.withState { gameState ->
            require(gameState.progress is GameProgress.Running) { "The game is not running" }

            saveGame(gameState)
            GameStateChange.GameOverChange(GameProgress.FinishedWithClockInfo(
                Win(GameWinReason.RESIGNATION, playerColor.opponent),
                getPlayerClock(gameState, PlayerColor.WHITE).toPausedAfterMove(),
                getPlayerClock(gameState, PlayerColor.BLACK).toPausedAfterMove()
            ))
        }
    }

    fun getGameInput(playerColor: PlayerColor): GameInput =
        LocalGameInput(playerColor)

    private fun getPlayerClock(state: GameState, color: PlayerColor): ClockState =
        state.getPlayerClock(color) ?: throw IllegalStateException("Player clock is null")

    private fun saveGame(state: GameState) {

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
