package pl.edu.uj.tcs.rchess

import pl.edu.uj.tcs.rchess.api.entity.Service
import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.GameResult
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.state.BoardState
import java.time.OffsetDateTime

data class UnsavedServiceGame(
    val moves: List<Move>,
    val startingPosition: BoardState,
    val creationDate: OffsetDateTime,
    val result: GameResult,
    val metadata: Map<String, String>,
    val gameIdInService: String?,
    val service: Service,
    val blackPlayer: ServiceAccount,
    val whitePlayer: ServiceAccount,
    val clockSettings: ClockSettings?
)