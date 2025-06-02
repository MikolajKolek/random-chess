package pl.edu.uj.tcs.rchess

import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.server.Server
import pl.edu.uj.tcs.rchess.view.RandomChessApp
import pl.edu.uj.tcs.rchess.view.showCriticalAppError

fun main() {
    val clientApi: ClientApi = try {
        Server()
    } catch (e: Exception) {
        e.printStackTrace()
        showCriticalAppError(e, true)
        return
    }

    // A try catch block around the Compose application() function call no longer re-throws exceptions,
    // so we cannot show out dialog in this case :/
    val app = RandomChessApp(clientApi)
    app.main()
}
