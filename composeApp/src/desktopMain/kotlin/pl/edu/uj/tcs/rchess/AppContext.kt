package pl.edu.uj.tcs.rchess

import pl.edu.uj.tcs.rchess.navigation.NavigationManager
import pl.edu.uj.tcs.rchess.server.ClientApi

class AppContext(
    val clientApi: ClientApi,
) {
    val navigation = NavigationManager()
}
