package pl.edu.uj.tcs.rchess.model.statemachine

/**
 * A message that is emitted when a change is applied in the state machine.
 * It contains the new state and the change that was applied.
 */
data class Update<T, C: Change<T>>(
    val state: T,
    val change: C?,
)
