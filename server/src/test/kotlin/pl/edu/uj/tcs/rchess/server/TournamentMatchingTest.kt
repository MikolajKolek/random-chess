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
        val unit = TournamentMatchingUnit(listOf(), playersPoints)
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
        val unit = TournamentMatchingUnit(limits, playersPoints)
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
        val unit = TournamentMatchingUnit(limits, playersPoints)
        val matches = unit.issueMatching()
        for(notAllowed in limits) {
            Assert.assertFalse(matches.first.contains(notAllowed))
        }
    }

    @Test
    fun simpleTournamentTest() {
        val playersPointsR1 = listOf(
            0.0 to "11",
            0.0 to "12",
            0.0 to "13",
            0.0 to "14",
            0.0 to "15",
            0.0 to "16",
            0.0 to "17",
            0.0 to "18",
            0.0 to "19",
            0.0 to "20",
            0.0 to "21",
            0.0 to "22",
            0.0 to "23"
        )
        var unit = TournamentMatchingUnit(listOf(), playersPointsR1)
        var matches = unit.issueMatching()

        println("ROUND 1:")
        for(match in matches.first) {
            println(match.first + '-' + match.second)
        }
        println("BYES: " + matches.second)

        val playersPointsR2 = listOf(
            1.0 to "11",
            1.0 to "12",
            0.0 to "13",
            1.0 to "14",
            0.0 to "15",
            1.0 to "16",
            0.0 to "17",
            1.0 to "18",
            0.0 to "19",
            1.0 to "20",
            0.0 to "21",
            1.0 to "22",
            0.0 to "23"
        )

        val limitsR2 = listOf(
            "12" to "13",
            "14" to "15",
            "16" to "17",
            "18" to "19",
            "20" to "21",
            "22" to "23"
        )

        unit = TournamentMatchingUnit(limitsR2, playersPointsR2)
        matches = unit.issueMatching()

        println("ROUND 2:")
        for(match in matches.first) {
            println(match.first + '-' + match.second)
        }
        println("BYES: " + matches.second)

        val playersPointsR3 = listOf(
            2.0 to "11",
            1.5 to "12",
            1.0 to "13",
            1.5 to "14",
            0.5 to "15",
            1.5 to "16",
            0.5 to "17",
            1.5 to "18",
            0.5 to "19",
            1.5 to "20",
            0.5 to "21",
            1.5 to "22",
            0.0 to "23"
        )

        val limitsR3 = listOf(
            "12" to "13",
            "14" to "15",
            "16" to "17",
            "18" to "19",
            "20" to "21",
            "22" to "23",
            "22" to "20",
            "18" to "16",
            "14" to "12",
            "11" to "23",
            "21" to "19",
            "17" to "15"
        )

        unit = TournamentMatchingUnit(limitsR3, playersPointsR3)
        matches = unit.issueMatching()

        println("ROUND 3:")
        for(match in matches.first) {
            println(match.first + '-' + match.second)
        }
        println("BYES: " + matches.second)

        val playersPointsR4 = listOf(
            3.0 to "11",
            2.5 to "12",
            2.0 to "13",
            2.0 to "14",
            0.5 to "15",
            2.0 to "16",
            1.0 to "17",
            2.0 to "18",
            1.0 to "19",
            2.0 to "20",
            0.5 to "21",
            1.5 to "22",
            1.0 to "23"
        )

        val limitsR4 = listOf(
            "12" to "13",
            "14" to "15",
            "16" to "17",
            "18" to "19",
            "20" to "21",
            "22" to "23",
            "22" to "20",
            "18" to "16",
            "14" to "12",
            "11" to "23",
            "21" to "19",
            "17" to "15",
            "11" to "22",
            "20" to "18",
            "16" to "14",
            "13" to "21",
            "19" to "17",
            "15" to "12"
        )

        unit = TournamentMatchingUnit(limitsR4, playersPointsR4)
        matches = unit.issueMatching()
        println("ROUND 4:")
        for(match in matches.first) {
            println(match.first + '-' + match.second)
        }
        println("BYES: " + matches.second)

        val playersPointsR5 = listOf(
            4.0 to "11",
            2.5 to "12",
            3.0 to "13",
            2.0 to "14",
            1.5 to "15",
            3.0 to "16",
            2.0 to "17",
            3.0 to "18",
            2.0 to "19",
            2.0 to "20",
            0.5 to "21",
            1.5 to "22",
            1.0 to "23"
        )

        val limitsR5 = listOf(
            "12" to "13",
            "14" to "15",
            "16" to "17",
            "18" to "19",
            "20" to "21",
            "22" to "23",
            "22" to "20",
            "18" to "16",
            "14" to "12",
            "11" to "23",
            "21" to "19",
            "17" to "15",
            "11" to "22",
            "20" to "18",
            "16" to "14",
            "13" to "21",
            "19" to "17",
            "15" to "12",
            "11" to "12",
            "20" to "16",
            "14" to "13",
            "18" to "22",
            "23" to "19",
            "17" to "21"
        )

        unit = TournamentMatchingUnit(limitsR5, playersPointsR5)
        matches = unit.issueMatching()
        println("ROUND 5:")
        for(match in matches.first) {
            println(match.first + '-' + match.second)
        }
        println("BYES: " + matches.second)
    }
}