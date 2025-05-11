package pl.edu.uj.tcs.rchess.model

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert
import org.junit.Test
import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.collections.mutableListOf

class PgnTest {
    @Test
    fun ambiguityTest() {
        Pgn("[Event \"Hourly SuperBlitz Arena\"]\n" +
                "[Site \"https://lichess.org/HafTJQ06\"]\n" +
                "[Date \"2025.05.08\"]\n" +
                "[White \"oussamabha\"]\n" +
                "[Black \"german11\"]\n" +
                "[Result \"1-0\"]\n" +
                "[GameId \"HafTJQ06\"]\n" +
                "[UTCDate \"2025.05.08\"]\n" +
                "[UTCTime \"22:08:17\"]\n" +
                "[WhiteElo \"1730\"]\n" +
                "[BlackElo \"1317\"]\n" +
                "[WhiteRatingDiff \"+1\"]\n" +
                "[BlackRatingDiff \"-1\"]\n" +
                "[Variant \"Standard\"]\n" +
                "[TimeControl \"180+0\"]\n" +
                "[ECO \"A41\"]\n" +
                "[Termination \"Normal\"]\n" +
                "\n" +
                "1. d4 d6 2. c4 Nd7 3. Nc3 c6 4. e4 e5 5. d5 cxd5 6. cxd5 Ngf6 7. Bd3 a6 8. Nf3 Nc5 9. Bc2 Be7 10. O-O Ncd7 11. h3 Qc7 12. Be3 b5 13. Rc1 b4 14. Ne2 Qd8 15. Nh2 a5 16. f4 g6 17. f5 gxf5 18. Rxf5 Nc5 19. Rf1 Ncxe4 20. Bh6 Bf8 21. Be3 Ba6 22. Ba4+ Ke7 23. Rc6 Bb7 24. Bb6 Qe8 25. Rc7+ Nd7 26. Rxb7 Nc5 27. Bxc5 dxc5 28. d6+ Ke6 29. Bxd7+ Qxd7 30. Qb3+ Kxd6 31. Rd1+ Kc6 32. Qa4+ Kxb7 33. Rxd7+ Kc8 34. Qc6+ Kb8 35. Qb7# 1-0")
    }

    @Test
    fun checkmateTest() {
        Pgn("[Event \"Hourly SuperBlitz Arena\"]\n" +
                "[Site \"https://lichess.org/hx0NGbtQ\"]\n" +
                "[Date \"2025.05.08\"]\n" +
                "[White \"bocah_nanas\"]\n" +
                "[Black \"german11\"]\n" +
                "[Result \"1-0\"]\n" +
                "[GameId \"hx0NGbtQ\"]\n" +
                "[UTCDate \"2025.05.08\"]\n" +
                "[UTCTime \"16:44:12\"]\n" +
                "[WhiteElo \"1812\"]\n" +
                "[BlackElo \"1353\"]\n" +
                "[WhiteRatingDiff \"+0\"]\n" +
                "[BlackRatingDiff \"-1\"]\n" +
                "[Variant \"Standard\"]\n" +
                "[TimeControl \"180+0\"]\n" +
                "[ECO \"C00\"]\n" +
                "[Termination \"Normal\"]\n" +
                "\n" +
                "1. e4 e6 2. d4 d6 3. Nf3 Nd7 4. Nc3 c6 5. Bd3 Qc7 6. Be3 e5 7. d5 Nc5 8. Qe2 Nxd3+ 9. cxd3 a6 10. d4 Be7 11. dxe5 dxe5 12. O-O-O Bd6 13. Qd2 Nf6 14. dxc6 Qxc6 15. Qxd6 Be6 16. Nxe5 Rd8 17. Qxd8# 1-0")
    }

    @Test
    fun fullMetadataTest() {
        val pgn = Pgn("[Event \"Hourly SuperBlitz Arena\"]\n" +
                "[Site \"https://lichess.org/BmicHSxE\"]\n" +
                "[Date \"2025.05.09\"]\n" +
                "[White \"german11\"]\n" +
                "[Black \"hipo01_02\"]\n" +
                "[Result \"0-1\"]\n" +
                "[GameId \"BmicHSxE\"]\n" +
                "[UTCDate \"2025.05.09\"]\n" +
                "[UTCTime \"08:45:05\"]\n" +
                "[WhiteElo \"1357\"]\n" +
                "[BlackElo \"2029\"]\n" +
                "[WhiteRatingDiff \"+0\"]\n" +
                "[BlackRatingDiff \"+1\"]\n" +
                "[Variant \"Standard\"]\n" +
                "[TimeControl \"180+0\"]\n" +
                "[ECO \"B08\"]\n" +
                "[Termination \"Time forfeit\"]\n" +
                "\n" +
                "1. e4 d6 2. Nf3 Nf6 3. Nc3 g6 4. d4 Bg7 5. Rb1 O-O 6. h3 c5 7. d5 Qa5 8. Bd2 Nfd7 9. Nb5 Qxa2 10. Nc7 Bxb2 11. Nxa8 Na6 12. Bd3 Nb4 13. Bxb4 cxb4 14. Nc7 a6 15. Kf1 Bc3 16. Ke2 Qa5 17. Ne6 fxe6 18. dxe6 Nc5 19. Ra1 Bxa1 20. Qxa1 Qxa1 21. Rxa1 Bxe6 22. Nd4 Bd7 23. Rb1 a5 24. Nb3 a4 25. Nxc5 dxc5 26. Bc4+ Kg7 27. Ke3 Kf6 28. f3 Ke5 29. f4+ Rxf4 30. Ke2 Rxe4+ 31. Kf3 Rxc4 32. Re1+ Kf6 33. Rf1 Rxc2 34. Ke4+ Bf5+ 0-1"
        )

        Assert.assertEquals(listOf(
            "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
            "rnbqkbnr/ppp1pppp/3p4/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2",
            "rnbqkbnr/ppp1pppp/3p4/8/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2",
            "rnbqkb1r/ppp1pppp/3p1n2/8/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3",
            "rnbqkb1r/ppp1pppp/3p1n2/8/4P3/2N2N2/PPPP1PPP/R1BQKB1R b KQkq - 3 3",
            "rnbqkb1r/ppp1pp1p/3p1np1/8/4P3/2N2N2/PPPP1PPP/R1BQKB1R w KQkq - 0 4",
            "rnbqkb1r/ppp1pp1p/3p1np1/8/3PP3/2N2N2/PPP2PPP/R1BQKB1R b KQkq d3 0 4",
            "rnbqk2r/ppp1ppbp/3p1np1/8/3PP3/2N2N2/PPP2PPP/R1BQKB1R w KQkq - 1 5",
            "rnbqk2r/ppp1ppbp/3p1np1/8/3PP3/2N2N2/PPP2PPP/1RBQKB1R b Kkq - 2 5",
            "rnbq1rk1/ppp1ppbp/3p1np1/8/3PP3/2N2N2/PPP2PPP/1RBQKB1R w K - 3 6",
            "rnbq1rk1/ppp1ppbp/3p1np1/8/3PP3/2N2N1P/PPP2PP1/1RBQKB1R b K - 0 6",
            "rnbq1rk1/pp2ppbp/3p1np1/2p5/3PP3/2N2N1P/PPP2PP1/1RBQKB1R w K c6 0 7",
            "rnbq1rk1/pp2ppbp/3p1np1/2pP4/4P3/2N2N1P/PPP2PP1/1RBQKB1R b K - 0 7",
            "rnb2rk1/pp2ppbp/3p1np1/q1pP4/4P3/2N2N1P/PPP2PP1/1RBQKB1R w K - 1 8",
            "rnb2rk1/pp2ppbp/3p1np1/q1pP4/4P3/2N2N1P/PPPB1PP1/1R1QKB1R b K - 2 8",
            "rnb2rk1/pp1nppbp/3p2p1/q1pP4/4P3/2N2N1P/PPPB1PP1/1R1QKB1R w K - 3 9",
            "rnb2rk1/pp1nppbp/3p2p1/qNpP4/4P3/5N1P/PPPB1PP1/1R1QKB1R b K - 4 9",
            "rnb2rk1/pp1nppbp/3p2p1/1NpP4/4P3/5N1P/qPPB1PP1/1R1QKB1R w K - 0 10",
            "rnb2rk1/ppNnppbp/3p2p1/2pP4/4P3/5N1P/qPPB1PP1/1R1QKB1R b K - 1 10",
            "rnb2rk1/ppNnpp1p/3p2p1/2pP4/4P3/5N1P/qbPB1PP1/1R1QKB1R w K - 0 11",
            "Nnb2rk1/pp1npp1p/3p2p1/2pP4/4P3/5N1P/qbPB1PP1/1R1QKB1R b K - 0 11",
            "N1b2rk1/pp1npp1p/n2p2p1/2pP4/4P3/5N1P/qbPB1PP1/1R1QKB1R w K - 1 12",
            "N1b2rk1/pp1npp1p/n2p2p1/2pP4/4P3/3B1N1P/qbPB1PP1/1R1QK2R b K - 2 12",
            "N1b2rk1/pp1npp1p/3p2p1/2pP4/1n2P3/3B1N1P/qbPB1PP1/1R1QK2R w K - 3 13",
            "N1b2rk1/pp1npp1p/3p2p1/2pP4/1B2P3/3B1N1P/qbP2PP1/1R1QK2R b K - 0 13",
            "N1b2rk1/pp1npp1p/3p2p1/3P4/1p2P3/3B1N1P/qbP2PP1/1R1QK2R w K - 0 14",
            "2b2rk1/ppNnpp1p/3p2p1/3P4/1p2P3/3B1N1P/qbP2PP1/1R1QK2R b K - 1 14",
            "2b2rk1/1pNnpp1p/p2p2p1/3P4/1p2P3/3B1N1P/qbP2PP1/1R1QK2R w K - 0 15",
            "2b2rk1/1pNnpp1p/p2p2p1/3P4/1p2P3/3B1N1P/qbP2PP1/1R1Q1K1R b - - 1 15",
            "2b2rk1/1pNnpp1p/p2p2p1/3P4/1p2P3/2bB1N1P/q1P2PP1/1R1Q1K1R w - - 2 16",
            "2b2rk1/1pNnpp1p/p2p2p1/3P4/1p2P3/2bB1N1P/q1P1KPP1/1R1Q3R b - - 3 16",
            "2b2rk1/1pNnpp1p/p2p2p1/q2P4/1p2P3/2bB1N1P/2P1KPP1/1R1Q3R w - - 4 17",
            "2b2rk1/1p1npp1p/p2pN1p1/q2P4/1p2P3/2bB1N1P/2P1KPP1/1R1Q3R b - - 5 17",
            "2b2rk1/1p1np2p/p2pp1p1/q2P4/1p2P3/2bB1N1P/2P1KPP1/1R1Q3R w - - 0 18",
            "2b2rk1/1p1np2p/p2pP1p1/q7/1p2P3/2bB1N1P/2P1KPP1/1R1Q3R b - - 0 18",
            "2b2rk1/1p2p2p/p2pP1p1/q1n5/1p2P3/2bB1N1P/2P1KPP1/1R1Q3R w - - 1 19",
            "2b2rk1/1p2p2p/p2pP1p1/q1n5/1p2P3/2bB1N1P/2P1KPP1/R2Q3R b - - 2 19",
            "2b2rk1/1p2p2p/p2pP1p1/q1n5/1p2P3/3B1N1P/2P1KPP1/b2Q3R w - - 0 20",
            "2b2rk1/1p2p2p/p2pP1p1/q1n5/1p2P3/3B1N1P/2P1KPP1/Q6R b - - 0 20",
            "2b2rk1/1p2p2p/p2pP1p1/2n5/1p2P3/3B1N1P/2P1KPP1/q6R w - - 0 21",
            "2b2rk1/1p2p2p/p2pP1p1/2n5/1p2P3/3B1N1P/2P1KPP1/R7 b - - 0 21",
            "5rk1/1p2p2p/p2pb1p1/2n5/1p2P3/3B1N1P/2P1KPP1/R7 w - - 0 22",
            "5rk1/1p2p2p/p2pb1p1/2n5/1p1NP3/3B3P/2P1KPP1/R7 b - - 1 22",
            "5rk1/1p1bp2p/p2p2p1/2n5/1p1NP3/3B3P/2P1KPP1/R7 w - - 2 23",
            "5rk1/1p1bp2p/p2p2p1/2n5/1p1NP3/3B3P/2P1KPP1/1R6 b - - 3 23",
            "5rk1/1p1bp2p/3p2p1/p1n5/1p1NP3/3B3P/2P1KPP1/1R6 w - - 0 24",
            "5rk1/1p1bp2p/3p2p1/p1n5/1p2P3/1N1B3P/2P1KPP1/1R6 b - - 1 24",
            "5rk1/1p1bp2p/3p2p1/2n5/pp2P3/1N1B3P/2P1KPP1/1R6 w - - 0 25",
            "5rk1/1p1bp2p/3p2p1/2N5/pp2P3/3B3P/2P1KPP1/1R6 b - - 0 25",
            "5rk1/1p1bp2p/6p1/2p5/pp2P3/3B3P/2P1KPP1/1R6 w - - 0 26",
            "5rk1/1p1bp2p/6p1/2p5/ppB1P3/7P/2P1KPP1/1R6 b - - 1 26",
            "5r2/1p1bp1kp/6p1/2p5/ppB1P3/7P/2P1KPP1/1R6 w - - 2 27",
            "5r2/1p1bp1kp/6p1/2p5/ppB1P3/4K2P/2P2PP1/1R6 b - - 3 27",
            "5r2/1p1bp2p/5kp1/2p5/ppB1P3/4K2P/2P2PP1/1R6 w - - 4 28",
            "5r2/1p1bp2p/5kp1/2p5/ppB1P3/4KP1P/2P3P1/1R6 b - - 0 28",
            "5r2/1p1bp2p/6p1/2p1k3/ppB1P3/4KP1P/2P3P1/1R6 w - - 1 29",
            "5r2/1p1bp2p/6p1/2p1k3/ppB1PP2/4K2P/2P3P1/1R6 b - - 0 29",
            "8/1p1bp2p/6p1/2p1k3/ppB1Pr2/4K2P/2P3P1/1R6 w - - 0 30",
            "8/1p1bp2p/6p1/2p1k3/ppB1Pr2/7P/2P1K1P1/1R6 b - - 1 30",
            "8/1p1bp2p/6p1/2p1k3/ppB1r3/7P/2P1K1P1/1R6 w - - 0 31",
            "8/1p1bp2p/6p1/2p1k3/ppB1r3/5K1P/2P3P1/1R6 b - - 1 31",
            "8/1p1bp2p/6p1/2p1k3/ppr5/5K1P/2P3P1/1R6 w - - 0 32",
            "8/1p1bp2p/6p1/2p1k3/ppr5/5K1P/2P3P1/4R3 b - - 1 32",
            "8/1p1bp2p/5kp1/2p5/ppr5/5K1P/2P3P1/4R3 w - - 2 33",
            "8/1p1bp2p/5kp1/2p5/ppr5/5K1P/2P3P1/5R2 b - - 3 33",
            "8/1p1bp2p/5kp1/2p5/pp6/5K1P/2r3P1/5R2 w - - 0 34",
            "8/1p1bp2p/5kp1/2p5/pp2K3/7P/2r3P1/5R2 b - - 1 34",
            "8/1p2p2p/5kp1/2p2b2/pp2K3/7P/2r3P1/5R2 w - - 2 35"
        ), boardStateFens(pgn))
        
        Assert.assertEquals(GameResult.BLACK_WON, pgn.result)
        Assert.assertEquals(
            mapOf(
                "Event" to "Hourly SuperBlitz Arena",
                "Site" to "https://lichess.org/BmicHSxE",
                "Date" to "2025.05.09",
                "GameId" to "BmicHSxE",
                "UTCDate" to "2025.05.09",
                "UTCTime" to "08:45:05",
                "WhiteElo" to "1357",
                "BlackElo" to "2029",
                "WhiteRatingDiff" to "+0",
                "BlackRatingDiff" to "+1",
                "Variant" to "Standard",
                "TimeControl" to "180+0",
                "ECO" to "B08",
                "Termination" to "Time forfeit"
            ),
            pgn.metadata!!.toMap().mapValues { it.value.jsonPrimitive.content }
        )
        Assert.assertEquals("hipo01_02", pgn.blackPlayerName)
        Assert.assertEquals("german11", pgn.whitePlayerName)
    }

    @Test
    fun pgnPositionsTest() {
        var pgn = Pgn("[Event \"Eastern Blitz Arena\"]\n" +
                "[Site \"https://lichess.org/DGTi2oOL\"]\n" +
                "[Date \"2025.05.09\"]\n" +
                "[White \"KALLOLB\"]\n" +
                "[Black \"german11\"]\n" +
                "[Result \"0-1\"]\n" +
                "[GameId \"DGTi2oOL\"]\n" +
                "[UTCDate \"2025.05.09\"]\n" +
                "[UTCTime \"07:21:29\"]\n" +
                "[WhiteElo \"1372\"]\n" +
                "[BlackElo \"1344\"]\n" +
                "[WhiteRatingDiff \"-6\"]\n" +
                "[BlackRatingDiff \"+6\"]\n" +
                "[Variant \"Standard\"]\n" +
                "[TimeControl \"300+0\"]\n" +
                "[ECO \"C41\"]\n" +
                "[Termination \"Time forfeit\"]\n" +
                "\n" +
                "1. e4 e5 2. Nf3 d6 3. Bc4 Nd7 4. O-O Be7 5. c3 c6 6. b4 Ngf6 7. d4 Qc7 8. Ng5 O-O 9. Qb3 h6 10. Nxf7 d5 11. exd5 Bd6 12. Nxd6 Qxd6 13. dxc6+ Kh8 14. cxd7 Bxd7 15. Re1 b5 16. Bf1 a6 17. Rxe5 Rfe8 18. Rxe8+ Rxe8 19. Be3 Be6 20. Qc2 Bc4 21. Bxc4 bxc4 22. Nd2 Rc8 23. Qg6 Qe6 24. Re1 Qe8 25. Qf5 Rd8 26. Kf1 Qh5 27. Qc5 Re8 28. Nf3 g5 29. Qd6 g4 30. Qxf6+ Kh7 31. Ne5 Rg8 32. Qe7+ Rg7 33. Qf8 Qxh2 34. Bf4 Qh1+ 35. Ke2 Qxg2 36. Bxh6 Qe4+ 37. Kf1 Qh1+ 0-1"
        )
        Assert.assertEquals(listOf(
            "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
            "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2",
            "rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2",
            "rnbqkbnr/ppp2ppp/3p4/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 0 3",
            "rnbqkbnr/ppp2ppp/3p4/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 1 3",
            "r1bqkbnr/pppn1ppp/3p4/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 2 4",
            "r1bqkbnr/pppn1ppp/3p4/4p3/2B1P3/5N2/PPPP1PPP/RNBQ1RK1 b kq - 3 4",
            "r1bqk1nr/pppnbppp/3p4/4p3/2B1P3/5N2/PPPP1PPP/RNBQ1RK1 w kq - 4 5",
            "r1bqk1nr/pppnbppp/3p4/4p3/2B1P3/2P2N2/PP1P1PPP/RNBQ1RK1 b kq - 0 5",
            "r1bqk1nr/pp1nbppp/2pp4/4p3/2B1P3/2P2N2/PP1P1PPP/RNBQ1RK1 w kq - 0 6",
            "r1bqk1nr/pp1nbppp/2pp4/4p3/1PB1P3/2P2N2/P2P1PPP/RNBQ1RK1 b kq b3 0 6",
            "r1bqk2r/pp1nbppp/2pp1n2/4p3/1PB1P3/2P2N2/P2P1PPP/RNBQ1RK1 w kq - 1 7",
            "r1bqk2r/pp1nbppp/2pp1n2/4p3/1PBPP3/2P2N2/P4PPP/RNBQ1RK1 b kq d3 0 7",
            "r1b1k2r/ppqnbppp/2pp1n2/4p3/1PBPP3/2P2N2/P4PPP/RNBQ1RK1 w kq - 1 8",
            "r1b1k2r/ppqnbppp/2pp1n2/4p1N1/1PBPP3/2P5/P4PPP/RNBQ1RK1 b kq - 2 8",
            "r1b2rk1/ppqnbppp/2pp1n2/4p1N1/1PBPP3/2P5/P4PPP/RNBQ1RK1 w - - 3 9",
            "r1b2rk1/ppqnbppp/2pp1n2/4p1N1/1PBPP3/1QP5/P4PPP/RNB2RK1 b - - 4 9",
            "r1b2rk1/ppqnbpp1/2pp1n1p/4p1N1/1PBPP3/1QP5/P4PPP/RNB2RK1 w - - 0 10",
            "r1b2rk1/ppqnbNp1/2pp1n1p/4p3/1PBPP3/1QP5/P4PPP/RNB2RK1 b - - 0 10",
            "r1b2rk1/ppqnbNp1/2p2n1p/3pp3/1PBPP3/1QP5/P4PPP/RNB2RK1 w - - 0 11",
            "r1b2rk1/ppqnbNp1/2p2n1p/3Pp3/1PBP4/1QP5/P4PPP/RNB2RK1 b - - 0 11",
            "r1b2rk1/ppqn1Np1/2pb1n1p/3Pp3/1PBP4/1QP5/P4PPP/RNB2RK1 w - - 1 12",
            "r1b2rk1/ppqn2p1/2pN1n1p/3Pp3/1PBP4/1QP5/P4PPP/RNB2RK1 b - - 0 12",
            "r1b2rk1/pp1n2p1/2pq1n1p/3Pp3/1PBP4/1QP5/P4PPP/RNB2RK1 w - - 0 13",
            "r1b2rk1/pp1n2p1/2Pq1n1p/4p3/1PBP4/1QP5/P4PPP/RNB2RK1 b - - 0 13",
            "r1b2r1k/pp1n2p1/2Pq1n1p/4p3/1PBP4/1QP5/P4PPP/RNB2RK1 w - - 1 14",
            "r1b2r1k/pp1P2p1/3q1n1p/4p3/1PBP4/1QP5/P4PPP/RNB2RK1 b - - 0 14",
            "r4r1k/pp1b2p1/3q1n1p/4p3/1PBP4/1QP5/P4PPP/RNB2RK1 w - - 0 15",
            "r4r1k/pp1b2p1/3q1n1p/4p3/1PBP4/1QP5/P4PPP/RNB1R1K1 b - - 1 15",
            "r4r1k/p2b2p1/3q1n1p/1p2p3/1PBP4/1QP5/P4PPP/RNB1R1K1 w - b6 0 16",
            "r4r1k/p2b2p1/3q1n1p/1p2p3/1P1P4/1QP5/P4PPP/RNB1RBK1 b - - 1 16",
            "r4r1k/3b2p1/p2q1n1p/1p2p3/1P1P4/1QP5/P4PPP/RNB1RBK1 w - - 0 17",
            "r4r1k/3b2p1/p2q1n1p/1p2R3/1P1P4/1QP5/P4PPP/RNB2BK1 b - - 0 17",
            "r3r2k/3b2p1/p2q1n1p/1p2R3/1P1P4/1QP5/P4PPP/RNB2BK1 w - - 1 18",
            "r3R2k/3b2p1/p2q1n1p/1p6/1P1P4/1QP5/P4PPP/RNB2BK1 b - - 0 18",
            "4r2k/3b2p1/p2q1n1p/1p6/1P1P4/1QP5/P4PPP/RNB2BK1 w - - 0 19",
            "4r2k/3b2p1/p2q1n1p/1p6/1P1P4/1QP1B3/P4PPP/RN3BK1 b - - 1 19",
            "4r2k/6p1/p2qbn1p/1p6/1P1P4/1QP1B3/P4PPP/RN3BK1 w - - 2 20",
            "4r2k/6p1/p2qbn1p/1p6/1P1P4/2P1B3/P1Q2PPP/RN3BK1 b - - 3 20",
            "4r2k/6p1/p2q1n1p/1p6/1PbP4/2P1B3/P1Q2PPP/RN3BK1 w - - 4 21",
            "4r2k/6p1/p2q1n1p/1p6/1PBP4/2P1B3/P1Q2PPP/RN4K1 b - - 0 21",
            "4r2k/6p1/p2q1n1p/8/1PpP4/2P1B3/P1Q2PPP/RN4K1 w - - 0 22",
            "4r2k/6p1/p2q1n1p/8/1PpP4/2P1B3/P1QN1PPP/R5K1 b - - 1 22",
            "2r4k/6p1/p2q1n1p/8/1PpP4/2P1B3/P1QN1PPP/R5K1 w - - 2 23",
            "2r4k/6p1/p2q1nQp/8/1PpP4/2P1B3/P2N1PPP/R5K1 b - - 3 23",
            "2r4k/6p1/p3qnQp/8/1PpP4/2P1B3/P2N1PPP/R5K1 w - - 4 24",
            "2r4k/6p1/p3qnQp/8/1PpP4/2P1B3/P2N1PPP/4R1K1 b - - 5 24",
            "2r1q2k/6p1/p4nQp/8/1PpP4/2P1B3/P2N1PPP/4R1K1 w - - 6 25",
            "2r1q2k/6p1/p4n1p/5Q2/1PpP4/2P1B3/P2N1PPP/4R1K1 b - - 7 25",
            "3rq2k/6p1/p4n1p/5Q2/1PpP4/2P1B3/P2N1PPP/4R1K1 w - - 8 26",
            "3rq2k/6p1/p4n1p/5Q2/1PpP4/2P1B3/P2N1PPP/4RK2 b - - 9 26",
            "3r3k/6p1/p4n1p/5Q1q/1PpP4/2P1B3/P2N1PPP/4RK2 w - - 10 27",
            "3r3k/6p1/p4n1p/2Q4q/1PpP4/2P1B3/P2N1PPP/4RK2 b - - 11 27",
            "4r2k/6p1/p4n1p/2Q4q/1PpP4/2P1B3/P2N1PPP/4RK2 w - - 12 28",
            "4r2k/6p1/p4n1p/2Q4q/1PpP4/2P1BN2/P4PPP/4RK2 b - - 13 28",
            "4r2k/8/p4n1p/2Q3pq/1PpP4/2P1BN2/P4PPP/4RK2 w - g6 0 29",
            "4r2k/8/p2Q1n1p/6pq/1PpP4/2P1BN2/P4PPP/4RK2 b - - 1 29",
            "4r2k/8/p2Q1n1p/7q/1PpP2p1/2P1BN2/P4PPP/4RK2 w - - 0 30",
            "4r2k/8/p4Q1p/7q/1PpP2p1/2P1BN2/P4PPP/4RK2 b - - 0 30",
            "4r3/7k/p4Q1p/7q/1PpP2p1/2P1BN2/P4PPP/4RK2 w - - 1 31",
            "4r3/7k/p4Q1p/4N2q/1PpP2p1/2P1B3/P4PPP/4RK2 b - - 2 31",
            "6r1/7k/p4Q1p/4N2q/1PpP2p1/2P1B3/P4PPP/4RK2 w - - 3 32",
            "6r1/4Q2k/p6p/4N2q/1PpP2p1/2P1B3/P4PPP/4RK2 b - - 4 32",
            "8/4Q1rk/p6p/4N2q/1PpP2p1/2P1B3/P4PPP/4RK2 w - - 5 33",
            "5Q2/6rk/p6p/4N2q/1PpP2p1/2P1B3/P4PPP/4RK2 b - - 6 33",
            "5Q2/6rk/p6p/4N3/1PpP2p1/2P1B3/P4PPq/4RK2 w - - 0 34",
            "5Q2/6rk/p6p/4N3/1PpP1Bp1/2P5/P4PPq/4RK2 b - - 1 34",
            "5Q2/6rk/p6p/4N3/1PpP1Bp1/2P5/P4PP1/4RK1q w - - 2 35",
            "5Q2/6rk/p6p/4N3/1PpP1Bp1/2P5/P3KPP1/4R2q b - - 3 35",
            "5Q2/6rk/p6p/4N3/1PpP1Bp1/2P5/P3KPq1/4R3 w - - 0 36",
            "5Q2/6rk/p6B/4N3/1PpP2p1/2P5/P3KPq1/4R3 b - - 0 36",
            "5Q2/6rk/p6B/4N3/1PpPq1p1/2P5/P3KP2/4R3 w - - 1 37",
            "5Q2/6rk/p6B/4N3/1PpPq1p1/2P5/P4P2/4RK2 b - - 2 37",
            "5Q2/6rk/p6B/4N3/1PpP2p1/2P5/P4P2/4RK1q w - - 3 38"
        ), boardStateFens(pgn))

        pgn = Pgn("[Event \"Eastern SuperBlitz Arena\"]\n" +
                "[Site \"https://lichess.org/0GgVT0T1\"]\n" +
                "[Date \"2025.05.09\"]\n" +
                "[White \"Waldschrat8\"]\n" +
                "[Black \"german11\"]\n" +
                "[Result \"1-0\"]\n" +
                "[GameId \"0GgVT0T1\"]\n" +
                "[UTCDate \"2025.05.09\"]\n" +
                "[UTCTime \"05:16:48\"]\n" +
                "[WhiteElo \"1963\"]\n" +
                "[BlackElo \"1318\"]\n" +
                "[WhiteRatingDiff \"+0\"]\n" +
                "[BlackRatingDiff \"+0\"]\n" +
                "[Variant \"Standard\"]\n" +
                "[TimeControl \"180+0\"]\n" +
                "[ECO \"C23\"]\n" +
                "[Termination \"Normal\"]\n" +
                "\n" +
                "1. e4 e5 2. Bc4 d6 3. Qf3 Qe7 4. Nc3 Nd7 5. d3 Ngf6 6. Bg5 Nc5 7. Bxf6 Qxf6 8. Qxf6 gxf6 9. f3 Be6 10. Bxe6 Nxe6 11. Nge2 Bh6 12. g4 Bf4 13. h3 Nd4 14. Nxd4 exd4 15. Ne2 Be3 16. b3 c5 17. Ng3 O-O 18. Ke2 Bf4 19. Nf5 Rad8 20. a4 h6 21. h4 Be5 22. h5 Rfe8 23. Nh4 Bf4 24. Ng2 Be3 25. Nxe3 dxe3 26. Kxe3 d5 27. Rhf1 d4+ 28. Kf2 Rb8 29. Kg3 Kf8 30. f4 Ke7 31. e5 fxe5 32. fxe5 Ke6 33. Rf5 f6 34. exf6 Rf8 35. Raf1 Rf7 36. Rxc5 Kd6 37. Rff5 b6 38. Rcd5+ Kc6 39. Rxd4 Rbb7 40. Re4 a5 41. Re6+ Kd7 42. Rfe5 Kc8 43. Re8+ Kd7 44. R8e7+ Rxe7 45. Rxe7+ Kc8 46. Rxb7 Kxb7 47. f7 1-0\n"
        )
        Assert.assertEquals(listOf(
            "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
            "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2",
            "rnbqkbnr/pppp1ppp/8/4p3/2B1P3/8/PPPP1PPP/RNBQK1NR b KQkq - 1 2",
            "rnbqkbnr/ppp2ppp/3p4/4p3/2B1P3/8/PPPP1PPP/RNBQK1NR w KQkq - 0 3",
            "rnbqkbnr/ppp2ppp/3p4/4p3/2B1P3/5Q2/PPPP1PPP/RNB1K1NR b KQkq - 1 3",
            "rnb1kbnr/ppp1qppp/3p4/4p3/2B1P3/5Q2/PPPP1PPP/RNB1K1NR w KQkq - 2 4",
            "rnb1kbnr/ppp1qppp/3p4/4p3/2B1P3/2N2Q2/PPPP1PPP/R1B1K1NR b KQkq - 3 4",
            "r1b1kbnr/pppnqppp/3p4/4p3/2B1P3/2N2Q2/PPPP1PPP/R1B1K1NR w KQkq - 4 5",
            "r1b1kbnr/pppnqppp/3p4/4p3/2B1P3/2NP1Q2/PPP2PPP/R1B1K1NR b KQkq - 0 5",
            "r1b1kb1r/pppnqppp/3p1n2/4p3/2B1P3/2NP1Q2/PPP2PPP/R1B1K1NR w KQkq - 1 6",
            "r1b1kb1r/pppnqppp/3p1n2/4p1B1/2B1P3/2NP1Q2/PPP2PPP/R3K1NR b KQkq - 2 6",
            "r1b1kb1r/ppp1qppp/3p1n2/2n1p1B1/2B1P3/2NP1Q2/PPP2PPP/R3K1NR w KQkq - 3 7",
            "r1b1kb1r/ppp1qppp/3p1B2/2n1p3/2B1P3/2NP1Q2/PPP2PPP/R3K1NR b KQkq - 0 7",
            "r1b1kb1r/ppp2ppp/3p1q2/2n1p3/2B1P3/2NP1Q2/PPP2PPP/R3K1NR w KQkq - 0 8",
            "r1b1kb1r/ppp2ppp/3p1Q2/2n1p3/2B1P3/2NP4/PPP2PPP/R3K1NR b KQkq - 0 8",
            "r1b1kb1r/ppp2p1p/3p1p2/2n1p3/2B1P3/2NP4/PPP2PPP/R3K1NR w KQkq - 0 9",
            "r1b1kb1r/ppp2p1p/3p1p2/2n1p3/2B1P3/2NP1P2/PPP3PP/R3K1NR b KQkq - 0 9",
            "r3kb1r/ppp2p1p/3pbp2/2n1p3/2B1P3/2NP1P2/PPP3PP/R3K1NR w KQkq - 1 10",
            "r3kb1r/ppp2p1p/3pBp2/2n1p3/4P3/2NP1P2/PPP3PP/R3K1NR b KQkq - 0 10",
            "r3kb1r/ppp2p1p/3pnp2/4p3/4P3/2NP1P2/PPP3PP/R3K1NR w KQkq - 0 11",
            "r3kb1r/ppp2p1p/3pnp2/4p3/4P3/2NP1P2/PPP1N1PP/R3K2R b KQkq - 1 11",
            "r3k2r/ppp2p1p/3pnp1b/4p3/4P3/2NP1P2/PPP1N1PP/R3K2R w KQkq - 2 12",
            "r3k2r/ppp2p1p/3pnp1b/4p3/4P1P1/2NP1P2/PPP1N2P/R3K2R b KQkq g3 0 12",
            "r3k2r/ppp2p1p/3pnp2/4p3/4PbP1/2NP1P2/PPP1N2P/R3K2R w KQkq - 1 13",
            "r3k2r/ppp2p1p/3pnp2/4p3/4PbP1/2NP1P1P/PPP1N3/R3K2R b KQkq - 0 13",
            "r3k2r/ppp2p1p/3p1p2/4p3/3nPbP1/2NP1P1P/PPP1N3/R3K2R w KQkq - 1 14",
            "r3k2r/ppp2p1p/3p1p2/4p3/3NPbP1/2NP1P1P/PPP5/R3K2R b KQkq - 0 14",
            "r3k2r/ppp2p1p/3p1p2/8/3pPbP1/2NP1P1P/PPP5/R3K2R w KQkq - 0 15",
            "r3k2r/ppp2p1p/3p1p2/8/3pPbP1/3P1P1P/PPP1N3/R3K2R b KQkq - 1 15",
            "r3k2r/ppp2p1p/3p1p2/8/3pP1P1/3PbP1P/PPP1N3/R3K2R w KQkq - 2 16",
            "r3k2r/ppp2p1p/3p1p2/8/3pP1P1/1P1PbP1P/P1P1N3/R3K2R b KQkq - 0 16",
            "r3k2r/pp3p1p/3p1p2/2p5/3pP1P1/1P1PbP1P/P1P1N3/R3K2R w KQkq c6 0 17",
            "r3k2r/pp3p1p/3p1p2/2p5/3pP1P1/1P1PbPNP/P1P5/R3K2R b KQkq - 1 17",
            "r4rk1/pp3p1p/3p1p2/2p5/3pP1P1/1P1PbPNP/P1P5/R3K2R w KQ - 2 18",
            "r4rk1/pp3p1p/3p1p2/2p5/3pP1P1/1P1PbPNP/P1P1K3/R6R b - - 3 18",
            "r4rk1/pp3p1p/3p1p2/2p5/3pPbP1/1P1P1PNP/P1P1K3/R6R w - - 4 19",
            "r4rk1/pp3p1p/3p1p2/2p2N2/3pPbP1/1P1P1P1P/P1P1K3/R6R b - - 5 19",
            "3r1rk1/pp3p1p/3p1p2/2p2N2/3pPbP1/1P1P1P1P/P1P1K3/R6R w - - 6 20",
            "3r1rk1/pp3p1p/3p1p2/2p2N2/P2pPbP1/1P1P1P1P/2P1K3/R6R b - a3 0 20",
            "3r1rk1/pp3p2/3p1p1p/2p2N2/P2pPbP1/1P1P1P1P/2P1K3/R6R w - - 0 21",
            "3r1rk1/pp3p2/3p1p1p/2p2N2/P2pPbPP/1P1P1P2/2P1K3/R6R b - - 0 21",
            "3r1rk1/pp3p2/3p1p1p/2p1bN2/P2pP1PP/1P1P1P2/2P1K3/R6R w - - 1 22",
            "3r1rk1/pp3p2/3p1p1p/2p1bN1P/P2pP1P1/1P1P1P2/2P1K3/R6R b - - 0 22",
            "3rr1k1/pp3p2/3p1p1p/2p1bN1P/P2pP1P1/1P1P1P2/2P1K3/R6R w - - 1 23",
            "3rr1k1/pp3p2/3p1p1p/2p1b2P/P2pP1PN/1P1P1P2/2P1K3/R6R b - - 2 23",
            "3rr1k1/pp3p2/3p1p1p/2p4P/P2pPbPN/1P1P1P2/2P1K3/R6R w - - 3 24",
            "3rr1k1/pp3p2/3p1p1p/2p4P/P2pPbP1/1P1P1P2/2P1K1N1/R6R b - - 4 24",
            "3rr1k1/pp3p2/3p1p1p/2p4P/P2pP1P1/1P1PbP2/2P1K1N1/R6R w - - 5 25",
            "3rr1k1/pp3p2/3p1p1p/2p4P/P2pP1P1/1P1PNP2/2P1K3/R6R b - - 0 25",
            "3rr1k1/pp3p2/3p1p1p/2p4P/P3P1P1/1P1PpP2/2P1K3/R6R w - - 0 26",
            "3rr1k1/pp3p2/3p1p1p/2p4P/P3P1P1/1P1PKP2/2P5/R6R b - - 0 26",
            "3rr1k1/pp3p2/5p1p/2pp3P/P3P1P1/1P1PKP2/2P5/R6R w - - 0 27",
            "3rr1k1/pp3p2/5p1p/2pp3P/P3P1P1/1P1PKP2/2P5/R4R2 b - - 1 27",
            "3rr1k1/pp3p2/5p1p/2p4P/P2pP1P1/1P1PKP2/2P5/R4R2 w - - 0 28",
            "3rr1k1/pp3p2/5p1p/2p4P/P2pP1P1/1P1P1P2/2P2K2/R4R2 b - - 1 28",
            "1r2r1k1/pp3p2/5p1p/2p4P/P2pP1P1/1P1P1P2/2P2K2/R4R2 w - - 2 29",
            "1r2r1k1/pp3p2/5p1p/2p4P/P2pP1P1/1P1P1PK1/2P5/R4R2 b - - 3 29",
            "1r2rk2/pp3p2/5p1p/2p4P/P2pP1P1/1P1P1PK1/2P5/R4R2 w - - 4 30",
            "1r2rk2/pp3p2/5p1p/2p4P/P2pPPP1/1P1P2K1/2P5/R4R2 b - - 0 30",
            "1r2r3/pp2kp2/5p1p/2p4P/P2pPPP1/1P1P2K1/2P5/R4R2 w - - 1 31",
            "1r2r3/pp2kp2/5p1p/2p1P2P/P2p1PP1/1P1P2K1/2P5/R4R2 b - - 0 31",
            "1r2r3/pp2kp2/7p/2p1p2P/P2p1PP1/1P1P2K1/2P5/R4R2 w - - 0 32",
            "1r2r3/pp2kp2/7p/2p1P2P/P2p2P1/1P1P2K1/2P5/R4R2 b - - 0 32",
            "1r2r3/pp3p2/4k2p/2p1P2P/P2p2P1/1P1P2K1/2P5/R4R2 w - - 1 33",
            "1r2r3/pp3p2/4k2p/2p1PR1P/P2p2P1/1P1P2K1/2P5/R7 b - - 2 33",
            "1r2r3/pp6/4kp1p/2p1PR1P/P2p2P1/1P1P2K1/2P5/R7 w - - 0 34",
            "1r2r3/pp6/4kP1p/2p2R1P/P2p2P1/1P1P2K1/2P5/R7 b - - 0 34",
            "1r3r2/pp6/4kP1p/2p2R1P/P2p2P1/1P1P2K1/2P5/R7 w - - 1 35",
            "1r3r2/pp6/4kP1p/2p2R1P/P2p2P1/1P1P2K1/2P5/5R2 b - - 2 35",
            "1r6/pp3r2/4kP1p/2p2R1P/P2p2P1/1P1P2K1/2P5/5R2 w - - 3 36",
            "1r6/pp3r2/4kP1p/2R4P/P2p2P1/1P1P2K1/2P5/5R2 b - - 0 36",
            "1r6/pp3r2/3k1P1p/2R4P/P2p2P1/1P1P2K1/2P5/5R2 w - - 1 37",
            "1r6/pp3r2/3k1P1p/2R2R1P/P2p2P1/1P1P2K1/2P5/8 b - - 2 37",
            "1r6/p4r2/1p1k1P1p/2R2R1P/P2p2P1/1P1P2K1/2P5/8 w - - 0 38",
            "1r6/p4r2/1p1k1P1p/3R1R1P/P2p2P1/1P1P2K1/2P5/8 b - - 1 38",
            "1r6/p4r2/1pk2P1p/3R1R1P/P2p2P1/1P1P2K1/2P5/8 w - - 2 39",
            "1r6/p4r2/1pk2P1p/5R1P/P2R2P1/1P1P2K1/2P5/8 b - - 0 39",
            "8/pr3r2/1pk2P1p/5R1P/P2R2P1/1P1P2K1/2P5/8 w - - 1 40",
            "8/pr3r2/1pk2P1p/5R1P/P3R1P1/1P1P2K1/2P5/8 b - - 2 40",
            "8/1r3r2/1pk2P1p/p4R1P/P3R1P1/1P1P2K1/2P5/8 w - a6 0 41",
            "8/1r3r2/1pk1RP1p/p4R1P/P5P1/1P1P2K1/2P5/8 b - - 1 41",
            "8/1r1k1r2/1p2RP1p/p4R1P/P5P1/1P1P2K1/2P5/8 w - - 2 42",
            "8/1r1k1r2/1p2RP1p/p3R2P/P5P1/1P1P2K1/2P5/8 b - - 3 42",
            "2k5/1r3r2/1p2RP1p/p3R2P/P5P1/1P1P2K1/2P5/8 w - - 4 43",
            "2k1R3/1r3r2/1p3P1p/p3R2P/P5P1/1P1P2K1/2P5/8 b - - 5 43",
            "4R3/1r1k1r2/1p3P1p/p3R2P/P5P1/1P1P2K1/2P5/8 w - - 6 44",
            "8/1r1kRr2/1p3P1p/p3R2P/P5P1/1P1P2K1/2P5/8 b - - 7 44",
            "8/1r1kr3/1p3P1p/p3R2P/P5P1/1P1P2K1/2P5/8 w - - 0 45",
            "8/1r1kR3/1p3P1p/p6P/P5P1/1P1P2K1/2P5/8 b - - 0 45",
            "2k5/1r2R3/1p3P1p/p6P/P5P1/1P1P2K1/2P5/8 w - - 1 46",
            "2k5/1R6/1p3P1p/p6P/P5P1/1P1P2K1/2P5/8 b - - 0 46",
            "8/1k6/1p3P1p/p6P/P5P1/1P1P2K1/2P5/8 w - - 0 47",
            "8/1k3P2/1p5p/p6P/P5P1/1P1P2K1/2P5/8 b - - 0 47"
        ), boardStateFens(pgn))

        pgn = Pgn("[Event \"Live Chess\"]\n" +
                "[Site \"Chess.com\"]\n" +
                "[Date \"2021.03.12\"]\n" +
                "[Round \"-\"]\n" +
                "[White \"Mikolaj_Kolek\"]\n" +
                "[Black \"coen_10\"]\n" +
                "[Result \"0-1\"]\n" +
                "[CurrentPosition \"8/5p2/3k4/4b1rp/8/4K3/P7/8 w - -\"]\n" +
                "[Timezone \"UTC\"]\n" +
                "[ECO \"B12\"]\n" +
                "[ECOUrl \"https://www.chess.com/openings/Caro-Kann-Defense-2.d4\"]\n" +
                "[UTCDate \"2021.03.12\"]\n" +
                "[UTCTime \"16:55:30\"]\n" +
                "[WhiteElo \"619\"]\n" +
                "[BlackElo \"804\"]\n" +
                "[TimeControl \"600\"]\n" +
                "[Termination \"coen_10 won on time\"]\n" +
                "[StartTime \"16:55:30\"]\n" +
                "[EndDate \"2021.03.12\"]\n" +
                "[EndTime \"17:14:04\"]\n" +
                "[Link \"https://www.chess.com/game/live/9307013805\"]\n" +
                "\n" +
                "1. d4 {[%clk 0:09:58.2]} 1... c6 {[%clk 0:09:49.3]} 2. e4 {[%clk 0:09:34.1]} 2... d5 {[%clk 0:09:47.2]} 3. Qf3 {[%clk 0:09:08.8]} 3... Nf6 {[%clk 0:09:28.7]} 4. exd5 {[%clk 0:08:46.7]} 4... Bg4 {[%clk 0:09:11.3]} 5. Qg3 {[%clk 0:08:26.7]} 5... cxd5 {[%clk 0:09:04.4]} 6. Bb5+ {[%clk 0:08:10.6]} 6... Nc6 {[%clk 0:08:59]} 7. Na3 {[%clk 0:07:38.9]} 7... a6 {[%clk 0:08:52.4]} 8. Bd3 {[%clk 0:07:16.6]} 8... Nxd4 {[%clk 0:08:38.4]} 9. Be3 {[%clk 0:06:40.1]} 9... Qa5+ {[%clk 0:08:14.3]} 10. c3 {[%clk 0:06:28.5]} 10... Ne6 {[%clk 0:07:00.6]} 11. Bf4 {[%clk 0:05:50.4]} 11... d4 {[%clk 0:06:42.5]} 12. Bc7 {[%clk 0:05:37.7]} 12... Nxc7 {[%clk 0:06:24.2]} 13. Nc4 {[%clk 0:05:23.3]} 13... Qc5 {[%clk 0:06:08.2]} 14. Rd1 {[%clk 0:04:37]} 14... Bxd1 {[%clk 0:06:03.9]} 15. Kxd1 {[%clk 0:04:32.8]} 15... Qh5+ {[%clk 0:05:55.3]} 16. f3 {[%clk 0:04:09.5]} 16... b5 {[%clk 0:05:41.7]} 17. Nb6 {[%clk 0:03:48.8]} 17... Rd8 {[%clk 0:05:25.1]} 18. Qxc7 {[%clk 0:03:41.8]} 18... dxc3 {[%clk 0:05:07]} 19. Qxc3 {[%clk 0:03:24.1]} 19... Ne4 {[%clk 0:04:49.3]} 20. Qc7 {[%clk 0:02:51]} 20... Nf2+ {[%clk 0:04:39.7]} 21. Ke1 {[%clk 0:02:38.7]} 21... Nxd3+ {[%clk 0:04:26.5]} 22. Kd2 {[%clk 0:02:10.2]} 22... Qc5 {[%clk 0:04:17.8]} 23. Qxc5 {[%clk 0:01:48.9]} 23... Nxc5+ {[%clk 0:04:13.8]} 24. Kc3 {[%clk 0:01:39.5]} 24... Nd3 {[%clk 0:04:08.9]} 25. Ne2 {[%clk 0:01:34.4]} 25... Nb4 {[%clk 0:04:00.8]} 26. Kxb4 {[%clk 0:01:06]} 26... e6+ {[%clk 0:03:54.6]} 27. Ka5 {[%clk 0:00:59.9]} 27... Rb8 {[%clk 0:03:45.7]} 28. Kxa6 {[%clk 0:00:58.5]} 28... Bc5 {[%clk 0:03:41.1]} 29. Kxb5 {[%clk 0:00:56.8]} 29... Bxb6 {[%clk 0:03:37.7]} 30. Rd1 {[%clk 0:00:43.3]} 30... Ke7 {[%clk 0:03:34.7]} 31. Nd4 {[%clk 0:00:36.2]} 31... Rhd8 {[%clk 0:03:24.9]} 32. Nxe6 {[%clk 0:00:25.5]} 32... Rxd1 {[%clk 0:03:21.6]} 33. Nxg7 {[%clk 0:00:17.1]} 33... Rd5+ {[%clk 0:03:13.6]} 34. Kb4 {[%clk 0:00:14]} 34... Bd4+ {[%clk 0:03:06]} 35. Kc4 {[%clk 0:00:13.9]} 35... Kd6 {[%clk 0:02:45.6]} 36. Kd3 {[%clk 0:00:13.8]} 36... Rg8 {[%clk 0:02:41.8]} 37. Ke2 {[%clk 0:00:11.9]} 37... Rxg7 {[%clk 0:02:39.8]} 38. Kf1 {[%clk 0:00:10.7]} 38... Bxb2 {[%clk 0:02:29.4]} 39. Kf2 {[%clk 0:00:10]} 39... Rd2+ {[%clk 0:02:23.9]} 40. Ke3 {[%clk 0:00:05.7]} 40... Rdxg2 {[%clk 0:02:18.5]} 41. Kf4 {[%clk 0:00:05.4]} 41... Rxh2 {[%clk 0:02:16.1]} 42. Kf5 {[%clk 0:00:04.5]} 42... Rh5+ {[%clk 0:02:11.1]} 43. Kf4 {[%clk 0:00:03.5]} 43... Be5+ {[%clk 0:02:04.8]} 44. Ke3 {[%clk 0:00:02.6]} 44... Rg3 {[%clk 0:02:01.5]} 45. Ke4 {[%clk 0:00:02.3]} 45... Rxf3 {[%clk 0:01:59]} 46. Kxf3 {[%clk 0:00:01.6]} 46... Rg5 {[%clk 0:01:54.1]} 47. Ke4 {[%clk 0:00:01.2]} 47... h6 {[%clk 0:01:52.5]} 48. Ke3 {[%clk 0:00:00.4]} 48... h5 {[%clk 0:01:50.5]} 0-1"
        )
        Assert.assertEquals(listOf(
            "rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq d3 0 1",
            "rnbqkbnr/pp1ppppp/2p5/8/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 0 2",
            "rnbqkbnr/pp1ppppp/2p5/8/3PP3/8/PPP2PPP/RNBQKBNR b KQkq e3 0 2",
            "rnbqkbnr/pp2pppp/2p5/3p4/3PP3/8/PPP2PPP/RNBQKBNR w KQkq d6 0 3",
            "rnbqkbnr/pp2pppp/2p5/3p4/3PP3/5Q2/PPP2PPP/RNB1KBNR b KQkq - 1 3",
            "rnbqkb1r/pp2pppp/2p2n2/3p4/3PP3/5Q2/PPP2PPP/RNB1KBNR w KQkq - 2 4",
            "rnbqkb1r/pp2pppp/2p2n2/3P4/3P4/5Q2/PPP2PPP/RNB1KBNR b KQkq - 0 4",
            "rn1qkb1r/pp2pppp/2p2n2/3P4/3P2b1/5Q2/PPP2PPP/RNB1KBNR w KQkq - 1 5",
            "rn1qkb1r/pp2pppp/2p2n2/3P4/3P2b1/6Q1/PPP2PPP/RNB1KBNR b KQkq - 2 5",
            "rn1qkb1r/pp2pppp/5n2/3p4/3P2b1/6Q1/PPP2PPP/RNB1KBNR w KQkq - 0 6",
            "rn1qkb1r/pp2pppp/5n2/1B1p4/3P2b1/6Q1/PPP2PPP/RNB1K1NR b KQkq - 1 6",
            "r2qkb1r/pp2pppp/2n2n2/1B1p4/3P2b1/6Q1/PPP2PPP/RNB1K1NR w KQkq - 2 7",
            "r2qkb1r/pp2pppp/2n2n2/1B1p4/3P2b1/N5Q1/PPP2PPP/R1B1K1NR b KQkq - 3 7",
            "r2qkb1r/1p2pppp/p1n2n2/1B1p4/3P2b1/N5Q1/PPP2PPP/R1B1K1NR w KQkq - 0 8",
            "r2qkb1r/1p2pppp/p1n2n2/3p4/3P2b1/N2B2Q1/PPP2PPP/R1B1K1NR b KQkq - 1 8",
            "r2qkb1r/1p2pppp/p4n2/3p4/3n2b1/N2B2Q1/PPP2PPP/R1B1K1NR w KQkq - 0 9",
            "r2qkb1r/1p2pppp/p4n2/3p4/3n2b1/N2BB1Q1/PPP2PPP/R3K1NR b KQkq - 1 9",
            "r3kb1r/1p2pppp/p4n2/q2p4/3n2b1/N2BB1Q1/PPP2PPP/R3K1NR w KQkq - 2 10",
            "r3kb1r/1p2pppp/p4n2/q2p4/3n2b1/N1PBB1Q1/PP3PPP/R3K1NR b KQkq - 0 10",
            "r3kb1r/1p2pppp/p3nn2/q2p4/6b1/N1PBB1Q1/PP3PPP/R3K1NR w KQkq - 1 11",
            "r3kb1r/1p2pppp/p3nn2/q2p4/5Bb1/N1PB2Q1/PP3PPP/R3K1NR b KQkq - 2 11",
            "r3kb1r/1p2pppp/p3nn2/q7/3p1Bb1/N1PB2Q1/PP3PPP/R3K1NR w KQkq - 0 12",
            "r3kb1r/1pB1pppp/p3nn2/q7/3p2b1/N1PB2Q1/PP3PPP/R3K1NR b KQkq - 1 12",
            "r3kb1r/1pn1pppp/p4n2/q7/3p2b1/N1PB2Q1/PP3PPP/R3K1NR w KQkq - 0 13",
            "r3kb1r/1pn1pppp/p4n2/q7/2Np2b1/2PB2Q1/PP3PPP/R3K1NR b KQkq - 1 13",
            "r3kb1r/1pn1pppp/p4n2/2q5/2Np2b1/2PB2Q1/PP3PPP/R3K1NR w KQkq - 2 14",
            "r3kb1r/1pn1pppp/p4n2/2q5/2Np2b1/2PB2Q1/PP3PPP/3RK1NR b Kkq - 3 14",
            "r3kb1r/1pn1pppp/p4n2/2q5/2Np4/2PB2Q1/PP3PPP/3bK1NR w Kkq - 0 15",
            "r3kb1r/1pn1pppp/p4n2/2q5/2Np4/2PB2Q1/PP3PPP/3K2NR b kq - 0 15",
            "r3kb1r/1pn1pppp/p4n2/7q/2Np4/2PB2Q1/PP3PPP/3K2NR w kq - 1 16",
            "r3kb1r/1pn1pppp/p4n2/7q/2Np4/2PB1PQ1/PP4PP/3K2NR b kq - 0 16",
            "r3kb1r/2n1pppp/p4n2/1p5q/2Np4/2PB1PQ1/PP4PP/3K2NR w kq b6 0 17",
            "r3kb1r/2n1pppp/pN3n2/1p5q/3p4/2PB1PQ1/PP4PP/3K2NR b kq - 1 17",
            "3rkb1r/2n1pppp/pN3n2/1p5q/3p4/2PB1PQ1/PP4PP/3K2NR w k - 2 18",
            "3rkb1r/2Q1pppp/pN3n2/1p5q/3p4/2PB1P2/PP4PP/3K2NR b k - 0 18",
            "3rkb1r/2Q1pppp/pN3n2/1p5q/8/2pB1P2/PP4PP/3K2NR w k - 0 19",
            "3rkb1r/4pppp/pN3n2/1p5q/8/2QB1P2/PP4PP/3K2NR b k - 0 19",
            "3rkb1r/4pppp/pN6/1p5q/4n3/2QB1P2/PP4PP/3K2NR w k - 1 20",
            "3rkb1r/2Q1pppp/pN6/1p5q/4n3/3B1P2/PP4PP/3K2NR b k - 2 20",
            "3rkb1r/2Q1pppp/pN6/1p5q/8/3B1P2/PP3nPP/3K2NR w k - 3 21",
            "3rkb1r/2Q1pppp/pN6/1p5q/8/3B1P2/PP3nPP/4K1NR b k - 4 21",
            "3rkb1r/2Q1pppp/pN6/1p5q/8/3n1P2/PP4PP/4K1NR w k - 0 22",
            "3rkb1r/2Q1pppp/pN6/1p5q/8/3n1P2/PP1K2PP/6NR b k - 1 22",
            "3rkb1r/2Q1pppp/pN6/1pq5/8/3n1P2/PP1K2PP/6NR w k - 2 23",
            "3rkb1r/4pppp/pN6/1pQ5/8/3n1P2/PP1K2PP/6NR b k - 0 23",
            "3rkb1r/4pppp/pN6/1pn5/8/5P2/PP1K2PP/6NR w k - 0 24",
            "3rkb1r/4pppp/pN6/1pn5/8/2K2P2/PP4PP/6NR b k - 1 24",
            "3rkb1r/4pppp/pN6/1p6/8/2Kn1P2/PP4PP/6NR w k - 2 25",
            "3rkb1r/4pppp/pN6/1p6/8/2Kn1P2/PP2N1PP/7R b k - 3 25",
            "3rkb1r/4pppp/pN6/1p6/1n6/2K2P2/PP2N1PP/7R w k - 4 26",
            "3rkb1r/4pppp/pN6/1p6/1K6/5P2/PP2N1PP/7R b k - 0 26",
            "3rkb1r/5ppp/pN2p3/1p6/1K6/5P2/PP2N1PP/7R w k - 0 27",
            "3rkb1r/5ppp/pN2p3/Kp6/8/5P2/PP2N1PP/7R b k - 1 27",
            "1r2kb1r/5ppp/pN2p3/Kp6/8/5P2/PP2N1PP/7R w k - 2 28",
            "1r2kb1r/5ppp/KN2p3/1p6/8/5P2/PP2N1PP/7R b k - 0 28",
            "1r2k2r/5ppp/KN2p3/1pb5/8/5P2/PP2N1PP/7R w k - 1 29",
            "1r2k2r/5ppp/1N2p3/1Kb5/8/5P2/PP2N1PP/7R b k - 0 29",
            "1r2k2r/5ppp/1b2p3/1K6/8/5P2/PP2N1PP/7R w k - 0 30",
            "1r2k2r/5ppp/1b2p3/1K6/8/5P2/PP2N1PP/3R4 b k - 1 30",
            "1r5r/4kppp/1b2p3/1K6/8/5P2/PP2N1PP/3R4 w - - 2 31",
            "1r5r/4kppp/1b2p3/1K6/3N4/5P2/PP4PP/3R4 b - - 3 31",
            "1r1r4/4kppp/1b2p3/1K6/3N4/5P2/PP4PP/3R4 w - - 4 32",
            "1r1r4/4kppp/1b2N3/1K6/8/5P2/PP4PP/3R4 b - - 0 32",
            "1r6/4kppp/1b2N3/1K6/8/5P2/PP4PP/3r4 w - - 0 33",
            "1r6/4kpNp/1b6/1K6/8/5P2/PP4PP/3r4 b - - 0 33",
            "1r6/4kpNp/1b6/1K1r4/8/5P2/PP4PP/8 w - - 1 34",
            "1r6/4kpNp/1b6/3r4/1K6/5P2/PP4PP/8 b - - 2 34",
            "1r6/4kpNp/8/3r4/1K1b4/5P2/PP4PP/8 w - - 3 35",
            "1r6/4kpNp/8/3r4/2Kb4/5P2/PP4PP/8 b - - 4 35",
            "1r6/5pNp/3k4/3r4/2Kb4/5P2/PP4PP/8 w - - 5 36",
            "1r6/5pNp/3k4/3r4/3b4/3K1P2/PP4PP/8 b - - 6 36",
            "6r1/5pNp/3k4/3r4/3b4/3K1P2/PP4PP/8 w - - 7 37",
            "6r1/5pNp/3k4/3r4/3b4/5P2/PP2K1PP/8 b - - 8 37",
            "8/5prp/3k4/3r4/3b4/5P2/PP2K1PP/8 w - - 0 38",
            "8/5prp/3k4/3r4/3b4/5P2/PP4PP/5K2 b - - 1 38",
            "8/5prp/3k4/3r4/8/5P2/Pb4PP/5K2 w - - 0 39",
            "8/5prp/3k4/3r4/8/5P2/Pb3KPP/8 b - - 1 39",
            "8/5prp/3k4/8/8/5P2/Pb1r1KPP/8 w - - 2 40",
            "8/5prp/3k4/8/8/4KP2/Pb1r2PP/8 b - - 3 40",
            "8/5prp/3k4/8/8/4KP2/Pb4rP/8 w - - 0 41",
            "8/5prp/3k4/8/5K2/5P2/Pb4rP/8 b - - 1 41",
            "8/5prp/3k4/8/5K2/5P2/Pb5r/8 w - - 0 42",
            "8/5prp/3k4/5K2/8/5P2/Pb5r/8 b - - 1 42",
            "8/5prp/3k4/5K1r/8/5P2/Pb6/8 w - - 2 43",
            "8/5prp/3k4/7r/5K2/5P2/Pb6/8 b - - 3 43",
            "8/5prp/3k4/4b2r/5K2/5P2/P7/8 w - - 4 44",
            "8/5prp/3k4/4b2r/8/4KP2/P7/8 b - - 5 44",
            "8/5p1p/3k4/4b2r/8/4KPr1/P7/8 w - - 6 45",
            "8/5p1p/3k4/4b2r/4K3/5Pr1/P7/8 b - - 7 45",
            "8/5p1p/3k4/4b2r/4K3/5r2/P7/8 w - - 0 46",
            "8/5p1p/3k4/4b2r/8/5K2/P7/8 b - - 0 46",
            "8/5p1p/3k4/4b1r1/8/5K2/P7/8 w - - 1 47",
            "8/5p1p/3k4/4b1r1/4K3/8/P7/8 b - - 2 47",
            "8/5p2/3k3p/4b1r1/4K3/8/P7/8 w - - 0 48",
            "8/5p2/3k3p/4b1r1/8/4K3/P7/8 b - - 1 48",
            "8/5p2/3k4/4b1rp/8/4K3/P7/8 w - - 0 49"
        ), boardStateFens(pgn))
    }

    @Test
    fun pgnDatabaseTest() {
        val pgns = Pgn.fromPgnDatabase(
            "[Event \"Hourly SuperBlitz Arena\"]\n" +
                    "[Site \"https://lichess.org/a1jAjKkw\"]\n" +
                    "[Date \"2025.02.28\"]\n" +
                    "[White \"claicybosh1\"]\n" +
                    "[Black \"german11\"]\n" +
                    "[Result \"1-0\"]\n" +
                    "[GameId \"a1jAjKkw\"]\n" +
                    "[UTCDate \"2025.02.28\"]\n" +
                    "[UTCTime \"11:49:33\"]\n" +
                    "[WhiteElo \"1616\"]\n" +
                    "[BlackElo \"1260\"]\n" +
                    "[WhiteRatingDiff \"+1\"]\n" +
                    "[BlackRatingDiff \"-1\"]\n" +
                    "[Variant \"Standard\"]\n" +
                    "[TimeControl \"180+0\"]\n" +
                    "[ECO \"C41\"]\n" +
                    "[Termination \"Time forfeit\"]\n" +
                    "\n" +
                    "1. e4 e5 2. Nf3 d6 3. d4 Nd7 4. dxe5 dxe5 5. Bb5 c6 6. Bc4 Qc7 7. Ng5 Nh6 8. Nf3 Nc5 9. Bxh6 gxh6 10. Nc3 Be6 11. Bxe6 Nxe6 12. Qd3 Nf4 13. Qf1 Bb4 14. g3 Bxc3+ 15. bxc3 Ne6 16. Qc4 O-O 17. O-O Rad8 18. Rab1 b6 19. Rfd1 Rxd1+ 20. Rxd1 Rd8 21. Rxd8+ Qxd8 22. Nxe5 Qd1+ 23. Kg2 Ng5 24. Qxc6 Qd8 25. Qxh6 Qe7 26. Ng4 Qxe4+ 27. f3 Qe2+ 28. Nf2 Qxf3+ 29. Kf1 Ne4 30. Qc1 1-0\n" +
                    "\n" +
                    "\n" +
                    "[Event \"Hourly SuperBlitz Arena\"]\n" +
                    "[Site \"https://lichess.org/V8LYsGpt\"]\n" +
                    "[Date \"2025.02.28\"]\n" +
                    "[White \"diegodv\"]\n" +
                    "[Black \"german11\"]\n" +
                    "[Result \"1-0\"]\n" +
                    "[GameId \"V8LYsGpt\"]\n" +
                    "[UTCDate \"2025.02.28\"]\n" +
                    "[UTCTime \"11:44:33\"]\n" +
                    "[WhiteElo \"1618\"]\n" +
                    "[BlackElo \"1262\"]\n" +
                    "[WhiteRatingDiff \"+1\"]\n" +
                    "[BlackRatingDiff \"-2\"]\n" +
                    "[Variant \"Standard\"]\n" +
                    "[TimeControl \"180+0\"]\n" +
                    "[ECO \"B00\"]\n" +
                    "[Termination \"Time forfeit\"]\n" +
                    "\n" +
                    "1. e4 d6 2. Nf3 Nd7 3. d4 c6 4. Bc4 Qc7 5. Bf4 e5 6. dxe5 dxe5 7. Be3 Be7 8. Bg5 Ngf6 9. Nc3 Nc5 10. O-O Be6 11. Bxe6 Nxe6 12. Qe2 b6 13. Rad1 Nxg5 14. Nxg5 h6 15. Nh3 O-O 16. f4 Nh7 17. fxe5 Qxe5 18. Rf5 Qe6 19. Rdf1 Bc5+ 20. Kh1 Rfe8 21. Qg4 g6 22. Nf4 Qc4 23. Rg1 Kh8 24. Rxc5 Qxc5 25. Nxg6+ 1-0\n" +
                    "\n" +
                    "\n" +
                    "[Event \"Hourly SuperBlitz Arena\"]\n" +
                    "[Site \"https://lichess.org/OFEF2MLo\"]\n" +
                    "[Date \"2025.02.28\"]\n" +
                    "[White \"german11\"]\n" +
                    "[Black \"TheTanke_86\"]\n" +
                    "[Result \"0-1\"]\n" +
                    "[GameId \"OFEF2MLo\"]\n" +
                    "[UTCDate \"2025.02.28\"]\n" +
                    "[UTCTime \"11:38:32\"]\n" +
                    "[WhiteElo \"1262\"]\n" +
                    "[BlackElo \"1717\"]\n" +
                    "[WhiteRatingDiff \"+0\"]\n" +
                    "[BlackRatingDiff \"+1\"]\n" +
                    "[Variant \"Standard\"]\n" +
                    "[TimeControl \"180+0\"]\n" +
                    "[ECO \"B32\"]\n" +
                    "[Termination \"Time forfeit\"]\n" +
                    "\n" +
                    "1. e4 c5 2. Nf3 Nc6 3. d4 cxd4 4. Nxd4 d6 5. Nf3 Nf6 6. Nc3 g6 7. h3 Bg7 8. Bd3 O-O 9. O-O Bd7 10. a3 Qc8 11. Be3 a6 12. Qd2 Ne5 13. Bh6 Nxf3+ 14. gxf3 Bxh3 15. Rfe1 Nh5 16. Kh2 e5 17. Ne2 f5 18. Bxg7 Kxg7 19. exf5 Bxf5 20. Rg1 Qc6 21. Bxf5 Rxf5 22. Ng3 Qxf3 23. Nxh5+ Qxh5+ 24. Kg2 Raf8 25. Rh1 Rxf2+ 0-1\n"
        )

        Assert.assertEquals(listOf(
            "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
            "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2",
            "rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2",
            "rnbqkbnr/ppp2ppp/3p4/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 0 3",
            "rnbqkbnr/ppp2ppp/3p4/4p3/3PP3/5N2/PPP2PPP/RNBQKB1R b KQkq d3 0 3",
            "r1bqkbnr/pppn1ppp/3p4/4p3/3PP3/5N2/PPP2PPP/RNBQKB1R w KQkq - 1 4",
            "r1bqkbnr/pppn1ppp/3p4/4P3/4P3/5N2/PPP2PPP/RNBQKB1R b KQkq - 0 4",
            "r1bqkbnr/pppn1ppp/8/4p3/4P3/5N2/PPP2PPP/RNBQKB1R w KQkq - 0 5",
            "r1bqkbnr/pppn1ppp/8/1B2p3/4P3/5N2/PPP2PPP/RNBQK2R b KQkq - 1 5",
            "r1bqkbnr/pp1n1ppp/2p5/1B2p3/4P3/5N2/PPP2PPP/RNBQK2R w KQkq - 0 6",
            "r1bqkbnr/pp1n1ppp/2p5/4p3/2B1P3/5N2/PPP2PPP/RNBQK2R b KQkq - 1 6",
            "r1b1kbnr/ppqn1ppp/2p5/4p3/2B1P3/5N2/PPP2PPP/RNBQK2R w KQkq - 2 7",
            "r1b1kbnr/ppqn1ppp/2p5/4p1N1/2B1P3/8/PPP2PPP/RNBQK2R b KQkq - 3 7",
            "r1b1kb1r/ppqn1ppp/2p4n/4p1N1/2B1P3/8/PPP2PPP/RNBQK2R w KQkq - 4 8",
            "r1b1kb1r/ppqn1ppp/2p4n/4p3/2B1P3/5N2/PPP2PPP/RNBQK2R b KQkq - 5 8",
            "r1b1kb1r/ppq2ppp/2p4n/2n1p3/2B1P3/5N2/PPP2PPP/RNBQK2R w KQkq - 6 9",
            "r1b1kb1r/ppq2ppp/2p4B/2n1p3/2B1P3/5N2/PPP2PPP/RN1QK2R b KQkq - 0 9",
            "r1b1kb1r/ppq2p1p/2p4p/2n1p3/2B1P3/5N2/PPP2PPP/RN1QK2R w KQkq - 0 10",
            "r1b1kb1r/ppq2p1p/2p4p/2n1p3/2B1P3/2N2N2/PPP2PPP/R2QK2R b KQkq - 1 10",
            "r3kb1r/ppq2p1p/2p1b2p/2n1p3/2B1P3/2N2N2/PPP2PPP/R2QK2R w KQkq - 2 11",
            "r3kb1r/ppq2p1p/2p1B2p/2n1p3/4P3/2N2N2/PPP2PPP/R2QK2R b KQkq - 0 11",
            "r3kb1r/ppq2p1p/2p1n2p/4p3/4P3/2N2N2/PPP2PPP/R2QK2R w KQkq - 0 12",
            "r3kb1r/ppq2p1p/2p1n2p/4p3/4P3/2NQ1N2/PPP2PPP/R3K2R b KQkq - 1 12",
            "r3kb1r/ppq2p1p/2p4p/4p3/4Pn2/2NQ1N2/PPP2PPP/R3K2R w KQkq - 2 13",
            "r3kb1r/ppq2p1p/2p4p/4p3/4Pn2/2N2N2/PPP2PPP/R3KQ1R b KQkq - 3 13",
            "r3k2r/ppq2p1p/2p4p/4p3/1b2Pn2/2N2N2/PPP2PPP/R3KQ1R w KQkq - 4 14",
            "r3k2r/ppq2p1p/2p4p/4p3/1b2Pn2/2N2NP1/PPP2P1P/R3KQ1R b KQkq - 0 14",
            "r3k2r/ppq2p1p/2p4p/4p3/4Pn2/2b2NP1/PPP2P1P/R3KQ1R w KQkq - 0 15",
            "r3k2r/ppq2p1p/2p4p/4p3/4Pn2/2P2NP1/P1P2P1P/R3KQ1R b KQkq - 0 15",
            "r3k2r/ppq2p1p/2p1n2p/4p3/4P3/2P2NP1/P1P2P1P/R3KQ1R w KQkq - 1 16",
            "r3k2r/ppq2p1p/2p1n2p/4p3/2Q1P3/2P2NP1/P1P2P1P/R3K2R b KQkq - 2 16",
            "r4rk1/ppq2p1p/2p1n2p/4p3/2Q1P3/2P2NP1/P1P2P1P/R3K2R w KQ - 3 17",
            "r4rk1/ppq2p1p/2p1n2p/4p3/2Q1P3/2P2NP1/P1P2P1P/R4RK1 b - - 4 17",
            "3r1rk1/ppq2p1p/2p1n2p/4p3/2Q1P3/2P2NP1/P1P2P1P/R4RK1 w - - 5 18",
            "3r1rk1/ppq2p1p/2p1n2p/4p3/2Q1P3/2P2NP1/P1P2P1P/1R3RK1 b - - 6 18",
            "3r1rk1/p1q2p1p/1pp1n2p/4p3/2Q1P3/2P2NP1/P1P2P1P/1R3RK1 w - - 0 19",
            "3r1rk1/p1q2p1p/1pp1n2p/4p3/2Q1P3/2P2NP1/P1P2P1P/1R1R2K1 b - - 1 19",
            "5rk1/p1q2p1p/1pp1n2p/4p3/2Q1P3/2P2NP1/P1P2P1P/1R1r2K1 w - - 0 20",
            "5rk1/p1q2p1p/1pp1n2p/4p3/2Q1P3/2P2NP1/P1P2P1P/3R2K1 b - - 0 20",
            "3r2k1/p1q2p1p/1pp1n2p/4p3/2Q1P3/2P2NP1/P1P2P1P/3R2K1 w - - 1 21",
            "3R2k1/p1q2p1p/1pp1n2p/4p3/2Q1P3/2P2NP1/P1P2P1P/6K1 b - - 0 21",
            "3q2k1/p4p1p/1pp1n2p/4p3/2Q1P3/2P2NP1/P1P2P1P/6K1 w - - 0 22",
            "3q2k1/p4p1p/1pp1n2p/4N3/2Q1P3/2P3P1/P1P2P1P/6K1 b - - 0 22",
            "6k1/p4p1p/1pp1n2p/4N3/2Q1P3/2P3P1/P1P2P1P/3q2K1 w - - 1 23",
            "6k1/p4p1p/1pp1n2p/4N3/2Q1P3/2P3P1/P1P2PKP/3q4 b - - 2 23",
            "6k1/p4p1p/1pp4p/4N1n1/2Q1P3/2P3P1/P1P2PKP/3q4 w - - 3 24",
            "6k1/p4p1p/1pQ4p/4N1n1/4P3/2P3P1/P1P2PKP/3q4 b - - 0 24",
            "3q2k1/p4p1p/1pQ4p/4N1n1/4P3/2P3P1/P1P2PKP/8 w - - 1 25",
            "3q2k1/p4p1p/1p5Q/4N1n1/4P3/2P3P1/P1P2PKP/8 b - - 0 25",
            "6k1/p3qp1p/1p5Q/4N1n1/4P3/2P3P1/P1P2PKP/8 w - - 1 26",
            "6k1/p3qp1p/1p5Q/6n1/4P1N1/2P3P1/P1P2PKP/8 b - - 2 26",
            "6k1/p4p1p/1p5Q/6n1/4q1N1/2P3P1/P1P2PKP/8 w - - 0 27",
            "6k1/p4p1p/1p5Q/6n1/4q1N1/2P2PP1/P1P3KP/8 b - - 0 27",
            "6k1/p4p1p/1p5Q/6n1/6N1/2P2PP1/P1P1q1KP/8 w - - 1 28",
            "6k1/p4p1p/1p5Q/6n1/8/2P2PP1/P1P1qNKP/8 b - - 2 28",
            "6k1/p4p1p/1p5Q/6n1/8/2P2qP1/P1P2NKP/8 w - - 0 29",
            "6k1/p4p1p/1p5Q/6n1/8/2P2qP1/P1P2N1P/5K2 b - - 1 29",
            "6k1/p4p1p/1p5Q/8/4n3/2P2qP1/P1P2N1P/5K2 w - - 2 30",
            "6k1/p4p1p/1p6/8/4n3/2P2qP1/P1P2N1P/2Q2K2 b - - 3 30"
        ), boardStateFens(pgns[0]))

        Assert.assertEquals(listOf(
            "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
            "rnbqkbnr/ppp1pppp/3p4/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2",
            "rnbqkbnr/ppp1pppp/3p4/8/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2",
            "r1bqkbnr/pppnpppp/3p4/8/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3",
            "r1bqkbnr/pppnpppp/3p4/8/3PP3/5N2/PPP2PPP/RNBQKB1R b KQkq d3 0 3",
            "r1bqkbnr/pp1npppp/2pp4/8/3PP3/5N2/PPP2PPP/RNBQKB1R w KQkq - 0 4",
            "r1bqkbnr/pp1npppp/2pp4/8/2BPP3/5N2/PPP2PPP/RNBQK2R b KQkq - 1 4",
            "r1b1kbnr/ppqnpppp/2pp4/8/2BPP3/5N2/PPP2PPP/RNBQK2R w KQkq - 2 5",
            "r1b1kbnr/ppqnpppp/2pp4/8/2BPPB2/5N2/PPP2PPP/RN1QK2R b KQkq - 3 5",
            "r1b1kbnr/ppqn1ppp/2pp4/4p3/2BPPB2/5N2/PPP2PPP/RN1QK2R w KQkq e6 0 6",
            "r1b1kbnr/ppqn1ppp/2pp4/4P3/2B1PB2/5N2/PPP2PPP/RN1QK2R b KQkq - 0 6",
            "r1b1kbnr/ppqn1ppp/2p5/4p3/2B1PB2/5N2/PPP2PPP/RN1QK2R w KQkq - 0 7",
            "r1b1kbnr/ppqn1ppp/2p5/4p3/2B1P3/4BN2/PPP2PPP/RN1QK2R b KQkq - 1 7",
            "r1b1k1nr/ppqnbppp/2p5/4p3/2B1P3/4BN2/PPP2PPP/RN1QK2R w KQkq - 2 8",
            "r1b1k1nr/ppqnbppp/2p5/4p1B1/2B1P3/5N2/PPP2PPP/RN1QK2R b KQkq - 3 8",
            "r1b1k2r/ppqnbppp/2p2n2/4p1B1/2B1P3/5N2/PPP2PPP/RN1QK2R w KQkq - 4 9",
            "r1b1k2r/ppqnbppp/2p2n2/4p1B1/2B1P3/2N2N2/PPP2PPP/R2QK2R b KQkq - 5 9",
            "r1b1k2r/ppq1bppp/2p2n2/2n1p1B1/2B1P3/2N2N2/PPP2PPP/R2QK2R w KQkq - 6 10",
            "r1b1k2r/ppq1bppp/2p2n2/2n1p1B1/2B1P3/2N2N2/PPP2PPP/R2Q1RK1 b kq - 7 10",
            "r3k2r/ppq1bppp/2p1bn2/2n1p1B1/2B1P3/2N2N2/PPP2PPP/R2Q1RK1 w kq - 8 11",
            "r3k2r/ppq1bppp/2p1Bn2/2n1p1B1/4P3/2N2N2/PPP2PPP/R2Q1RK1 b kq - 0 11",
            "r3k2r/ppq1bppp/2p1nn2/4p1B1/4P3/2N2N2/PPP2PPP/R2Q1RK1 w kq - 0 12",
            "r3k2r/ppq1bppp/2p1nn2/4p1B1/4P3/2N2N2/PPP1QPPP/R4RK1 b kq - 1 12",
            "r3k2r/p1q1bppp/1pp1nn2/4p1B1/4P3/2N2N2/PPP1QPPP/R4RK1 w kq - 0 13",
            "r3k2r/p1q1bppp/1pp1nn2/4p1B1/4P3/2N2N2/PPP1QPPP/3R1RK1 b kq - 1 13",
            "r3k2r/p1q1bppp/1pp2n2/4p1n1/4P3/2N2N2/PPP1QPPP/3R1RK1 w kq - 0 14",
            "r3k2r/p1q1bppp/1pp2n2/4p1N1/4P3/2N5/PPP1QPPP/3R1RK1 b kq - 0 14",
            "r3k2r/p1q1bpp1/1pp2n1p/4p1N1/4P3/2N5/PPP1QPPP/3R1RK1 w kq - 0 15",
            "r3k2r/p1q1bpp1/1pp2n1p/4p3/4P3/2N4N/PPP1QPPP/3R1RK1 b kq - 1 15",
            "r4rk1/p1q1bpp1/1pp2n1p/4p3/4P3/2N4N/PPP1QPPP/3R1RK1 w - - 2 16",
            "r4rk1/p1q1bpp1/1pp2n1p/4p3/4PP2/2N4N/PPP1Q1PP/3R1RK1 b - f3 0 16",
            "r4rk1/p1q1bppn/1pp4p/4p3/4PP2/2N4N/PPP1Q1PP/3R1RK1 w - - 1 17",
            "r4rk1/p1q1bppn/1pp4p/4P3/4P3/2N4N/PPP1Q1PP/3R1RK1 b - - 0 17",
            "r4rk1/p3bppn/1pp4p/4q3/4P3/2N4N/PPP1Q1PP/3R1RK1 w - - 0 18",
            "r4rk1/p3bppn/1pp4p/4qR2/4P3/2N4N/PPP1Q1PP/3R2K1 b - - 1 18",
            "r4rk1/p3bppn/1pp1q2p/5R2/4P3/2N4N/PPP1Q1PP/3R2K1 w - - 2 19",
            "r4rk1/p3bppn/1pp1q2p/5R2/4P3/2N4N/PPP1Q1PP/5RK1 b - - 3 19",
            "r4rk1/p4ppn/1pp1q2p/2b2R2/4P3/2N4N/PPP1Q1PP/5RK1 w - - 4 20",
            "r4rk1/p4ppn/1pp1q2p/2b2R2/4P3/2N4N/PPP1Q1PP/5R1K b - - 5 20",
            "r3r1k1/p4ppn/1pp1q2p/2b2R2/4P3/2N4N/PPP1Q1PP/5R1K w - - 6 21",
            "r3r1k1/p4ppn/1pp1q2p/2b2R2/4P1Q1/2N4N/PPP3PP/5R1K b - - 7 21",
            "r3r1k1/p4p1n/1pp1q1pp/2b2R2/4P1Q1/2N4N/PPP3PP/5R1K w - - 0 22",
            "r3r1k1/p4p1n/1pp1q1pp/2b2R2/4PNQ1/2N5/PPP3PP/5R1K b - - 1 22",
            "r3r1k1/p4p1n/1pp3pp/2b2R2/2q1PNQ1/2N5/PPP3PP/5R1K w - - 2 23",
            "r3r1k1/p4p1n/1pp3pp/2b2R2/2q1PNQ1/2N5/PPP3PP/6RK b - - 3 23",
            "r3r2k/p4p1n/1pp3pp/2b2R2/2q1PNQ1/2N5/PPP3PP/6RK w - - 4 24",
            "r3r2k/p4p1n/1pp3pp/2R5/2q1PNQ1/2N5/PPP3PP/6RK b - - 0 24",
            "r3r2k/p4p1n/1pp3pp/2q5/4PNQ1/2N5/PPP3PP/6RK w - - 0 25",
            "r3r2k/p4p1n/1pp3Np/2q5/4P1Q1/2N5/PPP3PP/6RK b - - 0 25"
        ), boardStateFens(pgns[1]))

        Assert.assertEquals(listOf(
            "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
            "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2",
            "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2",
            "r1bqkbnr/pp1ppppp/2n5/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3",
            "r1bqkbnr/pp1ppppp/2n5/2p5/3PP3/5N2/PPP2PPP/RNBQKB1R b KQkq d3 0 3",
            "r1bqkbnr/pp1ppppp/2n5/8/3pP3/5N2/PPP2PPP/RNBQKB1R w KQkq - 0 4",
            "r1bqkbnr/pp1ppppp/2n5/8/3NP3/8/PPP2PPP/RNBQKB1R b KQkq - 0 4",
            "r1bqkbnr/pp2pppp/2np4/8/3NP3/8/PPP2PPP/RNBQKB1R w KQkq - 0 5",
            "r1bqkbnr/pp2pppp/2np4/8/4P3/5N2/PPP2PPP/RNBQKB1R b KQkq - 1 5",
            "r1bqkb1r/pp2pppp/2np1n2/8/4P3/5N2/PPP2PPP/RNBQKB1R w KQkq - 2 6",
            "r1bqkb1r/pp2pppp/2np1n2/8/4P3/2N2N2/PPP2PPP/R1BQKB1R b KQkq - 3 6",
            "r1bqkb1r/pp2pp1p/2np1np1/8/4P3/2N2N2/PPP2PPP/R1BQKB1R w KQkq - 0 7",
            "r1bqkb1r/pp2pp1p/2np1np1/8/4P3/2N2N1P/PPP2PP1/R1BQKB1R b KQkq - 0 7",
            "r1bqk2r/pp2ppbp/2np1np1/8/4P3/2N2N1P/PPP2PP1/R1BQKB1R w KQkq - 1 8",
            "r1bqk2r/pp2ppbp/2np1np1/8/4P3/2NB1N1P/PPP2PP1/R1BQK2R b KQkq - 2 8",
            "r1bq1rk1/pp2ppbp/2np1np1/8/4P3/2NB1N1P/PPP2PP1/R1BQK2R w KQ - 3 9",
            "r1bq1rk1/pp2ppbp/2np1np1/8/4P3/2NB1N1P/PPP2PP1/R1BQ1RK1 b - - 4 9",
            "r2q1rk1/pp1bppbp/2np1np1/8/4P3/2NB1N1P/PPP2PP1/R1BQ1RK1 w - - 5 10",
            "r2q1rk1/pp1bppbp/2np1np1/8/4P3/P1NB1N1P/1PP2PP1/R1BQ1RK1 b - - 0 10",
            "r1q2rk1/pp1bppbp/2np1np1/8/4P3/P1NB1N1P/1PP2PP1/R1BQ1RK1 w - - 1 11",
            "r1q2rk1/pp1bppbp/2np1np1/8/4P3/P1NBBN1P/1PP2PP1/R2Q1RK1 b - - 2 11",
            "r1q2rk1/1p1bppbp/p1np1np1/8/4P3/P1NBBN1P/1PP2PP1/R2Q1RK1 w - - 0 12",
            "r1q2rk1/1p1bppbp/p1np1np1/8/4P3/P1NBBN1P/1PPQ1PP1/R4RK1 b - - 1 12",
            "r1q2rk1/1p1bppbp/p2p1np1/4n3/4P3/P1NBBN1P/1PPQ1PP1/R4RK1 w - - 2 13",
            "r1q2rk1/1p1bppbp/p2p1npB/4n3/4P3/P1NB1N1P/1PPQ1PP1/R4RK1 b - - 3 13",
            "r1q2rk1/1p1bppbp/p2p1npB/8/4P3/P1NB1n1P/1PPQ1PP1/R4RK1 w - - 0 14",
            "r1q2rk1/1p1bppbp/p2p1npB/8/4P3/P1NB1P1P/1PPQ1P2/R4RK1 b - - 0 14",
            "r1q2rk1/1p2ppbp/p2p1npB/8/4P3/P1NB1P1b/1PPQ1P2/R4RK1 w - - 0 15",
            "r1q2rk1/1p2ppbp/p2p1npB/8/4P3/P1NB1P1b/1PPQ1P2/R3R1K1 b - - 1 15",
            "r1q2rk1/1p2ppbp/p2p2pB/7n/4P3/P1NB1P1b/1PPQ1P2/R3R1K1 w - - 2 16",
            "r1q2rk1/1p2ppbp/p2p2pB/7n/4P3/P1NB1P1b/1PPQ1P1K/R3R3 b - - 3 16",
            "r1q2rk1/1p3pbp/p2p2pB/4p2n/4P3/P1NB1P1b/1PPQ1P1K/R3R3 w - e6 0 17",
            "r1q2rk1/1p3pbp/p2p2pB/4p2n/4P3/P2B1P1b/1PPQNP1K/R3R3 b - - 1 17",
            "r1q2rk1/1p4bp/p2p2pB/4pp1n/4P3/P2B1P1b/1PPQNP1K/R3R3 w - f6 0 18",
            "r1q2rk1/1p4Bp/p2p2p1/4pp1n/4P3/P2B1P1b/1PPQNP1K/R3R3 b - - 0 18",
            "r1q2r2/1p4kp/p2p2p1/4pp1n/4P3/P2B1P1b/1PPQNP1K/R3R3 w - - 0 19",
            "r1q2r2/1p4kp/p2p2p1/4pP1n/8/P2B1P1b/1PPQNP1K/R3R3 b - - 0 19",
            "r1q2r2/1p4kp/p2p2p1/4pb1n/8/P2B1P2/1PPQNP1K/R3R3 w - - 0 20",
            "r1q2r2/1p4kp/p2p2p1/4pb1n/8/P2B1P2/1PPQNP1K/R5R1 b - - 1 20",
            "r4r2/1p4kp/p1qp2p1/4pb1n/8/P2B1P2/1PPQNP1K/R5R1 w - - 2 21",
            "r4r2/1p4kp/p1qp2p1/4pB1n/8/P4P2/1PPQNP1K/R5R1 b - - 0 21",
            "r7/1p4kp/p1qp2p1/4pr1n/8/P4P2/1PPQNP1K/R5R1 w - - 0 22",
            "r7/1p4kp/p1qp2p1/4pr1n/8/P4PN1/1PPQ1P1K/R5R1 b - - 1 22",
            "r7/1p4kp/p2p2p1/4pr1n/8/P4qN1/1PPQ1P1K/R5R1 w - - 0 23",
            "r7/1p4kp/p2p2p1/4pr1N/8/P4q2/1PPQ1P1K/R5R1 b - - 0 23",
            "r7/1p4kp/p2p2p1/4pr1q/8/P7/1PPQ1P1K/R5R1 w - - 0 24",
            "r7/1p4kp/p2p2p1/4pr1q/8/P7/1PPQ1PK1/R5R1 b - - 1 24",
            "5r2/1p4kp/p2p2p1/4pr1q/8/P7/1PPQ1PK1/R5R1 w - - 2 25",
            "5r2/1p4kp/p2p2p1/4pr1q/8/P7/1PPQ1PK1/R6R b - - 3 25",
            "5r2/1p4kp/p2p2p1/4p2q/8/P7/1PPQ1rK1/R6R w - - 0 26"
        ), boardStateFens(pgns[2]))
    }

    // Set this to true if you want to run the external tests.
    // They require the presence of the pgn databases in desktopTest.
    val runExternalTests = false

    @Test
    fun fenExternalTest() {
        if(!runExternalTests)
            return

        val fenGeneratorPath = System.getProperty("user.dir") + "/src/desktopTest/fen_generator.py"
        val pgnDatabasePath = System.getProperty("user.dir") + "/src/desktopTest/pgn_database.pgn"

        val games = Pgn.fromPgnDatabase(File(pgnDatabasePath).readText())
        val generator = ProcessBuilder("python3", fenGeneratorPath, pgnDatabasePath).start()
        val reader = BufferedReader(InputStreamReader(generator.inputStream))
        for(game in games) {
            val json: JsonObject = Json.decodeFromString(reader.readLine())

            val fens = boardStateFens(game, true)
            json["fens"]?.jsonArray?.forEachIndexed { index, generatedFen ->
                Assert.assertEquals(generatedFen.jsonPrimitive.content, fens[index])
            }

            game.metadata?.jsonObject?.forEach { (key, value) ->
                Assert.assertEquals(json["headers"]?.jsonObject[key]?.jsonPrimitive?.content, value.jsonPrimitive.content)
            }

            Assert.assertEquals(json["white"]?.jsonPrimitive?.content, game.whitePlayerName)
            Assert.assertEquals(json["black"]?.jsonPrimitive?.content, game.blackPlayerName)
            Assert.assertEquals(json["result"]?.jsonPrimitive?.content, game.result.pgnString)
        }
    }

    @Test
    fun moveExternalTest() {
        if(!runExternalTests)
            return

        val moveCheckerPath = System.getProperty("user.dir") + "/src/desktopTest/move_checker.py"
        val pgnDatabasePath = System.getProperty("user.dir") + "/src/desktopTest/pgn_database_small.pgn"

        val games = Pgn.fromPgnDatabase(File(pgnDatabasePath).readText())
        val checker = ProcessBuilder("python3", moveCheckerPath, pgnDatabasePath).start()
        val reader = BufferedReader(InputStreamReader(checker.inputStream))
        for(game in games) {
            val jsonMoves: JsonArray = Json.decodeFromString(reader.readLine())

            boardStates(game, true).forEachIndexed { index, boardState ->
                Assert.assertEquals(
                    jsonMoves[index].jsonArray.map { it.jsonPrimitive.content }.sorted(),
                    (0..7).map { rank ->
                        (0..7).map { file ->
                            boardState.getLegalMovesFor(Square(rank, file))
                        }.flatten()
                    }.flatten().map { it.toLongAlgebraicNotation() }.sorted()
                )
            }
        }
    }

    fun boardStateFens(pgn: Pgn, includeFirstMove: Boolean = false): List<String> =
        boardStates(pgn, includeFirstMove).map { it.toFenString() }

    fun boardStates(pgn: Pgn, includeFirstMove: Boolean = false): List<BoardState> {
        val boardStates = mutableListOf(BoardState.initial())

        for(move in pgn.moves)
            boardStates.add(boardStates.last().applyMove(move))

        return if(includeFirstMove)
            boardStates
        else
            boardStates.drop(1)
    }
}