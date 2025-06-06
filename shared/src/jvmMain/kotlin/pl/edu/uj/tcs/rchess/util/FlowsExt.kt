package pl.edu.uj.tcs.rchess.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.runningFold

data class History<T>(
    val previous: T?,
    val current: T,
)

/**
 * Creates a flow mapping values in the flow to the values emitted before them.
 */
fun <T> Flow<T>.withPrev(): Flow<History<T>> =
    runningFold(
        initial = null as (History<T>?),
        operation = { accumulator, new -> History(accumulator?.current, new) }
    ).filterNotNull()
