package pl.edu.uj.tcs.rchess.api.entity.game

import pl.edu.uj.tcs.rchess.api.entity.Opening
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.state.BoardState
import java.time.LocalDateTime

/**
 * A game imported manually by the user and commited to the database
 */
data class PgnGame(
    override val id: Int,
    override val moves: List<Move>,
    override val startingPosition: BoardState,
    override val finalPosition: BoardState,
    override val opening: Opening,
    override val creationDate: LocalDateTime,
    override val result: GameResult,
    override val metadata: Map<String, String>,
    val blackPlayerName: String,
    val whitePlayerName: String,
    override val clockSettings: ClockSettings?,
) : HistoryGame() {
    override fun getPlayerName(playerColor: PlayerColor): String =
        when (playerColor) {
            PlayerColor.BLACK -> blackPlayerName
            PlayerColor.WHITE -> whitePlayerName
        }
}
