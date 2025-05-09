package pl.edu.uj.tcs.rchess.model

import kotlinx.coroutines.channels.Channel
import pl.edu.uj.tcs.rchess.model.observer.Change
import pl.edu.uj.tcs.rchess.model.observer.GameObserver
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
        writeWithListener("uci\n") { it.contains("uciok") }

        writeWithListener(options
            .map { "setoption name ${it.key} value ${it.value}\n" }
            .joinToString("") + "isready\n") { it.contains("readyok") }

        writeWithListener("ucinewgame\nisready\n") { it.contains("readyok") }
    }

    suspend fun playGame(gameObserver: GameObserver, gameInput: GameInput) {
        val channel = Channel<Change>(Channel.UNLIMITED)
        gameObserver.addChangeChannel(channel)

        while(true) {
            while(gameObserver.currentState.currentTurn != gameInput.getColor()) {
                if(channel.receive().isGameOver()) {
                    process.destroyForcibly()
                    return
                }
            }

            //TODO: IMPLEMENT winc and binc when/if they are implemented in ClockState
            var bestMove: Move? = null
            writeWithListener("position startpos moves "
                + gameObserver.moves.joinToString(" ") { it.toLongAlgebraicNotation() }
                + "\ngo wtime ${gameObserver.whiteClockState.remainingTime()} " +
                    "btime ${gameObserver.blackClockState.remainingTime()} " +
                    "winc 0 binc 0 " +
                    if(maxDepth != null) "depth $maxDepth " else "" +
                    if(moveTimeMs != null) "movetime $moveTimeMs\n" else ""
            ) {
                val bestMoveRegex = Regex("bestmove\\s+(\\S+)")
                val bestMoveMatch = bestMoveRegex.find(it)

                if(bestMoveMatch == null)
                    return@writeWithListener false

                bestMove = Move.fromLongAlgebraicNotation(bestMoveMatch.groupValues[1])
                return@writeWithListener true
            }

            gameInput.makeMove(bestMove!!)
        }
    }

    fun writeWithListener(commands: String, listener: (String) -> Boolean) {
        output.write(commands)
        output.flush()

        while(true) {
            val line = input.readLine() ?: break

            if(listener(line))
                break
        }
    }
}