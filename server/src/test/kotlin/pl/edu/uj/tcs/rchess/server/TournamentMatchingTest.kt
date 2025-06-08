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

    @Test
    fun limitedMatchingTest2() {
        val playersPoints = listOf(
            3.0 to "1",
            2.0 to "3",
            2.0 to "5",
            1.5 to "2",
            1.0 to "7",
            1.0 to "4",
            1.0 to "8",
            0.5 to "6"
        )
        val limits = listOf(
            "1" to "2",
            "3" to "4",
            "5" to "6",
            "7" to "8",
            "1" to "3",
            "2" to "4",
            "5" to "7",
            "6" to "8",
            "1" to "5",
            "3" to "7",
            "2" to "6",
            "4" to "8"
        )
        val unit = TournamentMatchingUnit(limits, 4, playersPoints)
        val matches = unit.issueMatching()
        for(notAllowed in limits) {
            Assert.assertFalse(matches.first.contains(notAllowed))
        }
    }
}