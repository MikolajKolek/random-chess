package pl.edu.uj.tcs.rchess.server

import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import java.time.LocalDateTime

data class ServiceGame(
    override val id: Int,
    override val moves: List<Move>,
    override val startingPosition: BoardState,
    override val finalPosition: BoardState,
    override val creationDate: LocalDateTime,
    override val result: GameResult,
    override val metadata: Map<String, String>,
    val gameIdInService: String?,
    val service: Service,
    val blackPlayer: ServiceAccount,
    val whitePlayer: ServiceAccount,
) : HistoryGame() {
    override fun getPlayerName(playerColor: PlayerColor): String =
        when (playerColor) {
            PlayerColor.BLACK -> blackPlayer.displayName
            PlayerColor.WHITE -> whitePlayer.displayName
        }
}
