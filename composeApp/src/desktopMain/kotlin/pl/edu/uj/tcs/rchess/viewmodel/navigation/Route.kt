package pl.edu.uj.tcs.rchess.viewmodel.navigation

sealed interface Route {
    data object GameHistory : Route
    data object Rankings : Route
    data class Ranking(val rankingId: Int) : Route
    data object TournamentList : Route
    data class Tournament(val tournamentId: Int) : Route
    data object Account : Route
}
