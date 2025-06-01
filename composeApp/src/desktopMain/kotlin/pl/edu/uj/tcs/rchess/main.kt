package pl.edu.uj.tcs.rchess

import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.server.Server
import pl.edu.uj.tcs.rchess.view.RandomChessApp

private val clientApi: ClientApi = Server()

fun main() {
    val app = RandomChessApp(clientApi)
    app.main()
}
