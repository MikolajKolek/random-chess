package pl.edu.uj.tcs.rchess.util

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * If the mutex was not locked, executes the given [action] under this mutex's lock.
 * Otherwise, immediately returns `null`.
 *
 * @param owner Optional owner token for debugging. When `owner` is specified (non-null value) and this mutex
 *        is already locked with the same token (same identity), this function throws [IllegalStateException].
 *
 * @return the return value of the action, or `null` if this mutex was not locked.
 * @see withLock
 */
inline fun <T> Mutex.tryWithLock(owner: Any? = null, action: () -> T): T? {
    val wasUnlocked = tryLock(owner)
    if(!wasUnlocked)
        return null

    return try {
        action()
    } finally {
        unlock(owner)
    }
}
