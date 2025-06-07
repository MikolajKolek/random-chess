package pl.edu.uj.tcs.rchess.server

import org.junit.Assert
import org.junit.Test
import pl.edu.uj.tcs.rchess.tournament.TournamentMatchingUnit

class TournamentMatchingTest {
    @Test
    fun simpleMatchingTest() {
        val playersPoints = listOf(
            3.0 to "A",
            2.0 to "B",
            2.0 to "C",
            1.5 to "D",
            1.5 to "E",
            1.0 to "F",
            1.0 to "G",
            0.0 to "H"
        )
        val unit = TournamentMatchingUnit(listOf(), 4, playersPoints)
        val matches = unit.issueMatching()
        Assert.assertEquals(0, matches.second.size)
        Assert.assertEquals(4, matches.first.size)
    }

    @Test
    fun limitedMatchingTest() {
        val playersPoints = listOf(
            3.0 to "A",
            2.0 to "B",
            2.0 to "C",
            1.5 to "D",
            1.5 to "E",
            1.0 to "F",
            1.0 to "G",
            0.0 to "H"
        )
        val limits = listOf(
            "A" to "B",
            "C" to "D",
            "E" to "F",
            "G" to "H",
            "A" to "D"
        )
        val unit = TournamentMatchingUnit(limits, 4, playersPoints)
        val matches = unit.issueMatching()
        for(notAllowed in limits) {
            Assert.assertFalse(matches.first.contains(notAllowed))
        }
        Assert.assertEquals(0, matches.second.size)
        Assert.assertEquals(4, matches.first.size)
    }
}