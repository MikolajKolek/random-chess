package pl.edu.uj.tcs.rchess.viewmodel.paging

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pl.edu.uj.tcs.rchess.util.logger
import pl.edu.uj.tcs.rchess.utils.waitUntil

/**
 * A generic class for fetching paginated data.
 * It manages loading and error states and allows pausing fetching,
 * until at least one observer is requesting more items.
 *
 * If an error occurs during fetching, it will be exposed in the [error] property,
 * and fetching will only continue after calling [dismissError].
 *
 * @param fetchPage a suspend function that loads the page based on the provided key.
 * Returns a [PageFetchResult] with the list of items and the key for the next page.
 * If there are no more pages, the next key should be set to null.
 */
class Paging<T, K>(
    private val scope: CoroutineScope,
    private val fetchPage: suspend (key: K?) -> PageFetchResult<T, K>,
) {
    private var _error by mutableStateOf<Exception?>(null)
    private var _loading by mutableStateOf(false)
    private val _list = mutableStateListOf<T>()
    private var reachedEnd = false
    private var nextKey: K? = null

    private val acceptingRequestStates = mutableStateListOf<State<Boolean>>()
    private var job = launchJob()
    private var refreshMutex = Mutex()

    private fun launchJob() = scope.launch {
        while (isActive && !reachedEnd) {
            waitUntil {
                _error == null && acceptingRequestStates.any { it.value }
            }

            val fetchKey = nextKey
            try {
                _loading = true

                val result = fetchPage(fetchKey)

                nextKey = result.nextPageKey
                if (result.nextPageKey == null) reachedEnd = true

                _list.addAll(result.items)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logger.error(e) { "Got an error while fetching page with key $fetchKey" }
                _error = e
            } finally {
                _loading = false
            }
        }
    }

    val error: Exception?
        get() = _error

    val loading: Boolean
        get() = _loading

    /**
     * Indicates that the first page is loading.
     */
    val initialLoading: Boolean
        get() = _list.isEmpty() && _loading

    /**
     * Clears all items and [error] and restarts fetching.
     */
    fun refresh() {
        scope.launch {
            refreshMutex.withLock {
                job.cancelAndJoin()
                reachedEnd = false
                nextKey = null
                _error = null
                _list.clear()
                job = launchJob()
            }
        }
    }

    /**
     * Clears [error] and allows fetching to continue.
     */
    fun dismissError() {
        _error = null
    }

    /**
     * Collects the list as a state, which can be used in a Composable function.
     *
     * @param acceptingRequests should indicate whether the current Composable is requesting more items.
     * This parameter can be used to pause fetching until the user has scrolled to the end of the list.
     */
    @Composable
    fun collectListAsState(acceptingRequests: State<Boolean>): State<List<T>> {
        DisposableEffect(acceptingRequests) {
            acceptingRequestStates.add(acceptingRequests)

            onDispose {
                acceptingRequestStates.remove(acceptingRequests)
            }
        }

        return remember { derivedStateOf { _list.toList() } }
    }
}
