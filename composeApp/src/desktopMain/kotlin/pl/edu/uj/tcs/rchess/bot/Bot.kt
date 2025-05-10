package pl.edu.uj.tcs.rchess.bot

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.takeWhile
import pl.edu.uj.tcs.rchess.model.GameInput
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.observer.GameObserver
import pl.edu.uj.tcs.rchess.model.state.GameProgress
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class Bot(private val process: Process,
          options: Map<String, String>,
          private val maxDepth: Int?,
          private val moveTimeMs: Int?
) {
    private val output: OutputStreamWriter = OutputStreamWriter(process.outputStream)
    private val input: BufferedReader = BufferedReader(InputStreamReader(process.inputStream))

    init {
        writeAndWaitUntil("uci\n") { it.contains("uciok") }

        writeAndWaitUntil(options
            .map { "setoption name ${it.key} value ${it.value}\n" }
            .joinToString("") + "isready\n") { it.contains("readyok") }

        writeAndWaitUntil("ucinewgame\nisready\n") { it.contains("readyok") }
    }

    // FIXME: Do we want to be able to use this function from multiple threads?
    //  Multiple calls to playGame will cause chaos
    suspend fun playGame(gameObserver: GameObserver, gameInput: GameInput) {
        // TODO: potential problem if multiple bots are called with the same gameInput
        gameObserver.messageFlow
            .takeWhile { update ->
                update.state.progress is GameProgress.Running
            }
            .filter { update ->
                update.state.currentState.currentTurn == gameInput.getColor()
            }
            .collect { update ->
                // TODO: If it's possible to never get a bestmove, the bot should send a resign command
                val bestMove = writeAndParse("position startpos moves "
                        + update.state.moves.joinToString(" ") { it.toLongAlgebraicNotation() }
                        + "\ngo wtime ${update.state.getPlayerClock(PlayerColor.WHITE).remainingTime().inWholeMilliseconds} " +
                        "btime ${update.state.getPlayerClock(PlayerColor.BLACK).remainingTime().inWholeMilliseconds} " +
                        "winc 0 binc 0 " +
                        if(maxDepth != null) "depth $maxDepth " else "" +
                                if(moveTimeMs != null) "movetime $moveTimeMs\n" else ""
                ) {
                    bestMoveRegex.find(it)?.let { bestMoveMatch ->
                        Move.fromLongAlgebraicNotation(bestMoveMatch.groupValues[1])
                    }
                }

                gameInput.makeMove(bestMove)
            }

        process.destroyForcibly()
    }

    /**
     * Sends a command to the engine and waits for a response.
     *
     * The listener is called for each line of the response.
     * The first non-null result is returned.
     */
    fun <T> writeAndParse(commands: String, listener: (String) -> T?): T {
        output.write(commands)
        output.flush()

        // TODO: Implement timeout mechanism?
        while(true) {
            val line = input.readLine() ?: throw EofException()

            val result = listener(line)
            if (result != null) return result
        }
    }

    fun writeAndWaitUntil(commands: String, condition: (String) -> Boolean) = writeAndParse<Unit>(commands) {
        if (condition(it)) Unit
        else null
    }

    companion object {
        private val bestMoveRegex = Regex("bestmove\\s+(\\S+)")
    }
}
