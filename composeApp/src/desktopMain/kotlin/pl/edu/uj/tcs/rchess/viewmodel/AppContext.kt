package pl.edu.uj.tcs.rchess.viewmodel

import pl.edu.uj.tcs.rchess.server.ClientApi
import pl.edu.uj.tcs.rchess.viewmodel.navigation.NavigationManager

class AppContext(
    val clientApi: ClientApi,
) {
    val navigation = NavigationManager()
}
