package pl.edu.uj.tcs.rchess.api.game

/**
 * A simple class storing references to a game observer and a game input for the same game.
 */
class PlayerGameControls(
    val observer: GameObserver,
    val input: GameInput,
)
