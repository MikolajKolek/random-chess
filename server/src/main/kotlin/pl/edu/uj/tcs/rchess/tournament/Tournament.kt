package pl.edu.uj.tcs.rchess.tournament

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import org.jooq.SQLDialect
import org.jooq.impl.DSL.using
import pl.edu.uj.tcs.rchess.config.ConfigLoader

internal class Tournament(
    val myId : Int
) {
    private val config = ConfigLoader.loadConfig()
    private val connection = ConnectionFactories.get(
        ConnectionFactoryOptions
            .parse("r2dbc:postgresql://${config.database.host}:${config.database.port}/${config.database.database}")
            .mutate()
            .option(ConnectionFactoryOptions.USER, config.database.user)
            .option(ConnectionFactoryOptions.PASSWORD, config.database.password)
            .build()
    )
    private val dsl = using(connection, SQLDialect.POSTGRES)

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
        // Add game entry to database
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
        // The logic for this is already complete in database.
        TODO()
    }
}