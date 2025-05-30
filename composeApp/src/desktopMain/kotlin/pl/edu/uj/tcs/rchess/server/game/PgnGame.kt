package pl.edu.uj.tcs.rchess.server.game

import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.state.BoardState
import pl.edu.uj.tcs.rchess.server.Opening
import java.time.LocalDateTime

/**
 * A game imported manually by the user and commited to the database
 */
data class PgnGame(
    override val id: Int,
    override val moves: List<Move>,
    override val startingPosition: BoardState,
//    override val finalPosition: BoardState,
    override val creationDate: LocalDateTime,
    override val result: GameResult,
    override val metadata: Map<String, String>,
    override val opening: Opening?,
    val blackPlayerName: String,
    val whitePlayerName: String,
) : HistoryGame() {
    override fun getPlayerName(playerColor: PlayerColor): String =
        when (playerColor) {
            PlayerColor.BLACK -> blackPlayerName
            PlayerColor.WHITE -> whitePlayerName
        }
}
