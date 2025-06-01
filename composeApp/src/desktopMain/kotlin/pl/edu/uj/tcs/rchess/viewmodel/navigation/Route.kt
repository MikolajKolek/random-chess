package pl.edu.uj.tcs.rchess.viewmodel.navigation

sealed interface Route {
    object GameHistory : Route
    object Rankings : Route
    data class Ranking(val rankingId: Int) : Route
    object TournamentList : Route
    data class Tournament(val tournamentId: Int) : Route
    object Account : Route
}
