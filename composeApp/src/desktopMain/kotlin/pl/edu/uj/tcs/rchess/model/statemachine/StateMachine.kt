package pl.edu.uj.tcs.rchess.model.statemachine

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A class implementing the state machine pattern where [T] represents the state
 * and [C] represents changes to the state.
 *
 * The only way to modify the state is by calling [withState] function.
 */
class StateMachine<T, C: Change<T>>(
    initialState: T
) {
    private var state = initialState
    private val stateMutex = Mutex()

    private val _updateFlow = MutableSharedFlow<Update<T, C>>(
        replay = 1,
        // We cannot drop updates, as they are necessary to apply changes sequentially
        onBufferOverflow = BufferOverflow.SUSPEND,
        extraBufferCapacity = 10, // arbitrary
    )

    /**
     * A flow that emits all updates to the state.
     */
    val updateFlow = _updateFlow.asSharedFlow()

    /**
     * Access the state and optionally apply a change to it
     */
    suspend fun withState(block: suspend (state: T) -> C?) {
        stateMutex.withLock {
            block(state)?.let { change ->
                state = change.applyTo(state)
                _updateFlow.emit(Update(state, change))
            }
        }
    }
}
