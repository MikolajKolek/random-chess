package pl.edu.uj.tcs.rchess.api.entity

import pl.edu.uj.tcs.rchess.model.state.BoardState

/**
 * Data class representing an opening identified for a game.
 *
 * @param eco ID of the opening in the Encyclopedia of Chess Openings (ECO).
 * @param name Name of the opening.
 * @param position The board state of the opening.
 * @param moveNumber The index of the move after which opening position was reached.
 */
data class Opening(
    val eco: String,
    val name: String,
    val position: BoardState,
    val moveNumber: Int,
) {
    /**
     * A URL in an ECO database where the user can see more details for this opening.
     */
    val url = "https://www.365chess.com/eco/${eco}"
}
