package pl.edu.uj.tcs.rchess.api.entity

import pl.edu.uj.tcs.rchess.model.state.BoardState

/**
 * Data class representing an opening identified for a game.
 *
 * @param eco ID of the opening in the Encyclopedia of Chess Openings (ECO).
 * @param name Name of the opening.
 * @param position The board state after the last opening move.
 * @param moveNumber The index of the move after which opening position was reached.
 */
data class Opening(
    val eco: String,
    val name: String,
    val position: BoardState,
    val moveNumber: Int,
)
