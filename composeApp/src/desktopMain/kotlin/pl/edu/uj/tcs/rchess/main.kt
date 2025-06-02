package pl.edu.uj.tcs.rchess

import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.server.Server
import pl.edu.uj.tcs.rchess.view.RandomChessApp
import pl.edu.uj.tcs.rchess.view.startInitialErrorApp

fun main() {
    val clientApi: ClientApi = try {
        Server()
    } catch (e: Exception) {
        e.printStackTrace()
        startInitialErrorApp(e)
        return
    }
    val app = RandomChessApp(clientApi)
    app.main()
}
