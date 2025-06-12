package pl.edu.uj.tcs.rchess.api.entity.game

import pl.edu.uj.tcs.rchess.api.entity.Opening
import pl.edu.uj.tcs.rchess.api.entity.PlayerDetails
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.state.BoardState
import java.time.OffsetDateTime

/**
 * A game imported manually by the user and committed to the database
 */
data class PgnGame(
    override val id: Int,
    override val moves: List<Move>,
    override val startingPosition: BoardState,
    override val finalPosition: BoardState,
    override val opening: Opening?,
    override val creationDate: OffsetDateTime,
    override val result: GameResult,
    override val metadata: Map<String, String>,
    override val blackPlayer: PlayerDetails.Simple,
    override val whitePlayer: PlayerDetails.Simple,
    override val clockSettings: ClockSettings?,
) : HistoryGame() {
    override fun getPlayer(color: PlayerColor): PlayerDetails.Simple =
        when (color) {
            PlayerColor.BLACK -> blackPlayer
            PlayerColor.WHITE -> whitePlayer
        }
}
