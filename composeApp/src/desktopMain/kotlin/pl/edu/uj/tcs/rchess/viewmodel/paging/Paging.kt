package pl.edu.uj.tcs.rchess.viewmodel.paging

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pl.edu.uj.tcs.rchess.util.logger
import pl.edu.uj.tcs.rchess.utils.waitUntil

/**
 * A generic class and interface for fetching paginated data.
 * It manages loading and error states and allows pausing fetching,
 * until at least one observer is requesting more items.
 *
 * If an error occurs during fetching, it will be exposed in the [error] property,
 * and fetching will only continue after calling [dismissError].
 *
 * The interface is used to hide the K generic parameter of the [PagingImpl] from [Paging] users.
 */
interface Paging<T> {
    val error: Exception?

    val loading: Boolean

    /**
     * Indicates that the first page is loading.
     */
    val initialLoading: Boolean

    /**
     * Clears all items and [error] and restarts fetching.
     */
    fun refresh()

    /**
     * Clears [error] and allows fetching to continue.
     */
    fun dismissError()

    /**
     * Collects the list as a state, which can be used in a Composable function.
     *
     * @param acceptingRequests should indicate whether the current Composable is requesting more items.
     * This parameter can be used to pause fetching until the user has scrolled to the end of the list.
     */
    @Composable
    fun collectListAsState(acceptingRequests: State<Boolean>): State<List<T>>
}

/**
 * Creates a [Paging] instance.
 *
 * @param fetchPage a suspend function that loads the page based on the provided key.
 * Returns a [PageFetchResult] with the list of items and the key for the next page.
 * If there are no more pages, the next key should be set to null.
 */
fun <T, K> Paging(
    scope: CoroutineScope,
    fetchPage: suspend (key: K?) -> PageFetchResult<T, K>,
): Paging<T> = PagingImpl(scope, fetchPage)

private class PagingImpl<T, K>(
    private val scope: CoroutineScope,
    private val fetchPage: suspend (key: K?) -> PageFetchResult<T, K>,
): Paging<T> {
    private var _error by mutableStateOf<Exception?>(null)
    private var _loading by mutableStateOf(true)
    private val _list = mutableStateListOf<T>()
    private var reachedEnd = false
    private var nextKey: K? = null

    private val acceptingRequestStates = mutableStateListOf<State<Boolean>>()
    private var job = launchJob()
    private var refreshMutex = Mutex()

    private fun launchJob() = scope.launch {
        while (isActive && !reachedEnd) {
            waitUntil {
                _error == null && (_list.isEmpty() || acceptingRequestStates.any { it.value })
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
        _loading = false
    }

    override val error: Exception?
        get() = _error

    override val loading: Boolean
        get() = _loading

    override val initialLoading: Boolean
        get() = _list.isEmpty() && _loading

    override fun refresh() {
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

    override fun dismissError() {
        _error = null
    }

    @Composable
    override fun collectListAsState(acceptingRequests: State<Boolean>): State<List<T>> {
        DisposableEffect(acceptingRequests, this) {
            acceptingRequestStates.add(acceptingRequests)

            onDispose {
                acceptingRequestStates.remove(acceptingRequests)
            }
        }

        return remember(this) { derivedStateOf { _list.toList() } }
    }
}
