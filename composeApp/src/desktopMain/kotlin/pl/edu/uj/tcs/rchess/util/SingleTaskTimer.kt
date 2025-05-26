package pl.edu.uj.tcs.rchess.util

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

/**
 * A facility used for creating timers. Only one timer can be active at a time,
 * meaning that when a new timer is added, the previous one is replaced.
 *
 * This class gives a guarantee that when a timer starts execution, it will not
 * be interrupted or replaced mid-way, instead waiting for the replacement
 * until task execution finishes.
 */
class SingleTaskTimer(context: CoroutineContext) {
    val timerScope = CoroutineScope(context)
    val taskMutex = Mutex()
    var timer: Job? = null

    /**
     * Replace the currently set timer with [task] executed after [delay].
     *
     * If the previously set timer has not yet started execution, it is stopped, and
     * it will never execute. If it is currently executing, this function will
     * suspend until the previous task finishes execution.
     */
    suspend fun replaceTask(delay: Duration, task: suspend CoroutineScope.() -> Unit) {
        taskMutex.withLock {
            timer?.cancel()

            timer = timerScope.launch {
                delay(delay)

                taskMutex.withLock {
                    if(!isActive)
                        return@launch

                    task()
                }
            }
        }
    }

    /**
     * Stops the timer. After this function is called, this object should not be used again.
     */
    fun stop() {
        timerScope.cancel()
    }
}