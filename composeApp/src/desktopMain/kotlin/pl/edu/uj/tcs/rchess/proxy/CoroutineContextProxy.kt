package pl.edu.uj.tcs.rchess.proxy

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.edu.uj.tcs.rchess.api.ClientApi

/**
 * This class makes sure all [ClientApi] are called with `withContext(Dispatchers.IO)`
 * to make sure they don't run on the main thread.
 */
class CoroutineContextProxy(
    server: ClientApi,
) : AbstractProxy(server) {
    override suspend fun <T> proxy(action: suspend () -> T): T {
        return withContext(Dispatchers.IO) {
            action()
        }
    }
}
