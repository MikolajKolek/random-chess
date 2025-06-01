package pl.edu.uj.tcs.rchess.viewmodel

import pl.edu.uj.tcs.rchess.api.ClientApi
import pl.edu.uj.tcs.rchess.viewmodel.datastate.DataStateViewModel
import pl.edu.uj.tcs.rchess.viewmodel.navigation.NavigationManager

class AppContext(
    val clientApi: ClientApi,
) {
    val navigation = NavigationManager()

    val gameListViewModel = DataStateViewModel { clientApi.getUserGames() }
}
