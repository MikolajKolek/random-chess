package pl.edu.uj.tcs.rchess.model.game

import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor

class LocalGameInput(
    override val playerColor: PlayerColor,
    val liveGame: LiveGame
) : GameInput {
    override fun makeMove(move: Move) {
        liveGame.makeMove(move, playerColor)
    }

    override fun resign() {
        TODO("Not yet implemented")
    }
}