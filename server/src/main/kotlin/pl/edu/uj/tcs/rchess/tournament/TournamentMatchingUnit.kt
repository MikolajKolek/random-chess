package pl.edu.uj.tcs.rchess.tournament

/**
 * Currently, the matching unit uses a variation of the Monrad system.
 */
internal class TournamentMatchingUnit(
    val playedGames: List<Pair<String, String>>, // Temporary solution
    val playersPoints: List<Pair<Double, String>> // Temporary solution
) {

    /**
     * @param group The list of players to match up
     * @return A pair containing a matching for the next round and a list of unmatched players
     */
    fun pairUpGroup(group : List<String>) : Pair<List<Pair<String, String>>, List<String>> {
        val result : MutableList<Pair<String, String>> = mutableListOf()
        val matched : Array<Boolean> = Array(group.size) { false }
        val matchFailed : MutableList<String> = mutableListOf()
        var currentMatched : Int = group.size - 1
        var nextMatched : Int

        while(currentMatched >= 0) {
            if(matched[currentMatched]) {
                currentMatched--
                continue
            }
            nextMatched = currentMatched - 1
            while(nextMatched >= 0) {
                if(
                    !playedGames.contains(Pair(group[currentMatched], group[nextMatched]))
                    && !playedGames.contains(Pair(group[nextMatched], group[currentMatched]))
                    && !matched[nextMatched]
                ) {
                    matched[currentMatched] = true
                    matched[nextMatched] = true
                    result += Pair(group[currentMatched], group[nextMatched])
                    currentMatched--
                    break
                }
                nextMatched--
            }
            if(!matched[currentMatched]) matchFailed += group[currentMatched]
            currentMatched--
        }

        return Pair(result, matchFailed)
    }

    /**
     * @return The list of matchings for the current round and a list of unmatched players
     */
    fun issueMatching() : Pair<List<Pair<String, String>>, List<String>> {
        val groupedPlayers: List<Pair<Double, MutableList<String>>> = playersPoints
            .groupBy { it.first }
            .mapValues { entry -> entry.value.map { it.second }.toMutableList() }
            .toList()
            .sortedByDescending { it.first }
        val matchings : MutableList<Pair<String, String>> = mutableListOf()
        var matched : Pair<List<Pair<String, String>>, List<String>>
        var remaining : List<String> = listOf()

        for(playerGroup in groupedPlayers) {
            playerGroup.second += remaining
            matched = pairUpGroup(playerGroup.second)
            remaining = matched.second
            matchings += matched.first
        }

        return Pair(matchings, remaining)
    }
}