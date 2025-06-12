package pl.edu.uj.tcs.rchess.tournament

import pl.edu.uj.tcs.rchess.server.Database

/**
 * We didn't manage to finish this class in time. We decided to leave this preliminary,
 * first draft of the code for you to see.
 *
 * Do note that much would have to change to finish this class - for example, it currently
 * lacks any database interaction and operates on pairs instead of actual named
 * data classes.
 */
internal class Tournament(
    val tournamentId : Int,
    val database: Database
) {
    val players : HashSet<String> = HashSet()
    var matchings : MutableList<Pair<String, String>> = mutableListOf()
    val playedGames : MutableList<Pair<String, String>> = mutableListOf()
    val playersPoints : HashMap<String, Double> = HashMap()
    var round = 0

    fun playerJoin(player: String) {
        // TODO: Add player to database
        require(!players.contains(player)) { "Already joined the tournament." }
        players.add(player)
        playersPoints.putIfAbsent(player, 0.0)
    }

    fun playerLeave(player: String) {
        // No need to remove players from database entries - it is better to leave them there.
        require(players.contains(player)) { "Not registered in the tournament." }
        players.add(player)
    }

    fun registerGame(game: Pair<Pair<String, String>, String>) {
        require(matchings.contains(game.first)) { "Game not registered in current matchings." }
        require(players.contains(game.first.first) && players.contains(game.first.second))
        require(listOf("0-1", "1/2-1/2", "1-0", "*").contains(game.second)) { "Game result unrecognized." }
        matchings.remove(game.first)
        when (game.second) {
            "*" -> {
                return
            }
            "0-1" -> {
                playersPoints.put(game.first.second, playersPoints.get(game.first.second)!!.plus(1.0))
            }
            "1-0" -> {
                playersPoints.put(game.first.first, playersPoints.get(game.first.first)!!.plus(1.0))
            }
            else -> {
                playersPoints.put(game.first.second, playersPoints.get(game.first.second)!!.plus(0.5))
                playersPoints.put(game.first.first, playersPoints.get(game.first.first)!!.plus(0.5))
            }
        }
        playedGames.add(game.first)
        // Add game entry to the database
    }

    fun issueMatchings() {
        require(matchings.isEmpty()) { "Games from previous round are not finished." }
        val pointsList = playersPoints.map{ (key, value)->Pair(value, key)}.toList()
        val matcher = TournamentMatchingUnit(playedGames, pointsList)
        val resultMatchings = matcher.issueMatching()
        // For all players from resultMatchings.second, put them into byes.
        round++
        matchings = resultMatchings.first.toMutableList()
    }

    fun getStandings() {
        // The logic for this is already complete in the database.
        TODO()
    }
}