package pl.edu.uj.tcs.rchess.model.statemachine

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private var _stateFlow = MutableStateFlow(initialState)
    private val stateMutex = Mutex()

    private val _updateFlow = MutableSharedFlow<Update<T, C>>(
        replay = 1,
        // We cannot drop updates, as they are necessary to apply changes sequentially
        onBufferOverflow = BufferOverflow.SUSPEND,
        extraBufferCapacity = 10, // arbitrary
    )

    init {
        val emitSuccess = _updateFlow.tryEmit(Update(initialState, null))
        if (!emitSuccess) throw IllegalStateException("Failed to emit initial state in StateMachine")
    }

    /**
     * A flow that emits all updates to the state.
     */
    val updateFlow = _updateFlow.asSharedFlow()

    val stateFlow = _stateFlow.asStateFlow()

    /**
     * Access the state and optionally apply a change to it.
     *
     * This function uses an internal lock, so it's thread-safe and can be called from multiple coroutines.
     */
    suspend fun withState(block: suspend (state: T) -> C?): T {
        return stateMutex.withLock {
            block(_stateFlow.value)?.let { change ->
                _stateFlow.value = change.applyTo(_stateFlow.value)
                _updateFlow.emit(Update(_stateFlow.value, change))
            }
            _stateFlow.value
        }
    }

    /**
     * Access the state
     */
    suspend fun getState() = withState { null }
}
