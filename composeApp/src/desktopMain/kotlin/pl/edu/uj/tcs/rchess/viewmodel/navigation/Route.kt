package pl.edu.uj.tcs.rchess.viewmodel.navigation

sealed interface Route {
    object NewGame : Route
    object GameHistory : Route
    object RankingList : Route
    data class Ranking(val rankingId: Int) : Route
    object TournamentList : Route
    data class Tournament(val tournamentId: Int) : Route
    object Account : Route
}
