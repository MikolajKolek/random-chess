package pl.edu.uj.tcs.rchess.model

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import pl.edu.uj.tcs.rchess.model.Fen.Companion.fromFen
import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString
import pl.edu.uj.tcs.rchess.model.pieces.King

class BoardStateTest {
    @get:Rule
    val rule = createComposeRule()

    companion object {
        fun BoardState.testAlgebraicMove(move: String, expectedFen: String): BoardState {
            Assert.assertEquals(move, this.movetoStandardAlgebraic(this.standardAlgebraicToMove(move)))
            return this.applyStandardAlgebraicMove(move).also {
                Assert.assertEquals(expectedFen, it.toFenString())
            }
        }
    }

    @Test
    fun emptyPositionTest() {
        BoardState.empty()
        Assert.assertThrows(IllegalArgumentException::class.java) { BoardState.empty().isLegal() }
    }

    @Test
    fun initialPositionTest() {
        BoardState.fromFen(Fen.INITIAL)
        BoardState.initial().toFenString()
        Assert.assertEquals(BoardState.initial().board[Square.fromString("e1")]!!.javaClass, King(PlayerColor.WHITE).javaClass)
    }

    @Test
    fun enPassantTest1() {
        val fenString = "2r3k1/1q1nbppp/r3p3/3pP3/pPpP4/P1Q2N2/2RN1PPP/2R4K b - b3 0 23"
        val board = BoardState.fromFen(fenString)

        Assert.assertTrue(
            board.getLegalMovesFor(Square(3, 0))
                .contains(Move(Square(3, 0), Square(2, 1)))
        )
        Assert.assertTrue(
            board.getLegalMovesFor(Square(3, 2))
                .contains(Move(Square(3, 2), Square(2, 1)))
        )
    }

    // Moves from:
    //  https://www.chessprogramming.org/En_passant#En_passant_bugs
    // Partial FENs generated using:
    //  https://masterinchess.com/pgn-to-fen-string-converter
    @Test
    fun enPassantTest2() {
        BoardState.initial()
            .testAlgebraicMove("e4", "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1")
            .testAlgebraicMove("e6", "rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2")
            .testAlgebraicMove("d4", "rnbqkbnr/pppp1ppp/4p3/8/3PP3/8/PPP2PPP/RNBQKBNR b KQkq d3 0 2")
            .testAlgebraicMove("d5", "rnbqkbnr/ppp2ppp/4p3/3p4/3PP3/8/PPP2PPP/RNBQKBNR w KQkq d6 0 3")
            .testAlgebraicMove("Nd2", "rnbqkbnr/ppp2ppp/4p3/3p4/3PP3/8/PPPN1PPP/R1BQKBNR b KQkq - 1 3")
            .testAlgebraicMove("Nf6", "rnbqkb1r/ppp2ppp/4pn2/3p4/3PP3/8/PPPN1PPP/R1BQKBNR w KQkq - 2 4")
            .testAlgebraicMove("e5", "rnbqkb1r/ppp2ppp/4pn2/3pP3/3P4/8/PPPN1PPP/R1BQKBNR b KQkq - 0 4")
            .testAlgebraicMove("Nfd7", "rnbqkb1r/pppn1ppp/4p3/3pP3/3P4/8/PPPN1PPP/R1BQKBNR w KQkq - 1 5")
            .testAlgebraicMove("Bd3", "rnbqkb1r/pppn1ppp/4p3/3pP3/3P4/3B4/PPPN1PPP/R1BQK1NR b KQkq - 2 5")
            .testAlgebraicMove("b6", "rnbqkb1r/p1pn1ppp/1p2p3/3pP3/3P4/3B4/PPPN1PPP/R1BQK1NR w KQkq - 0 6")
            .testAlgebraicMove("Ngf3", "rnbqkb1r/p1pn1ppp/1p2p3/3pP3/3P4/3B1N2/PPPN1PPP/R1BQK2R b KQkq - 1 6")
            .testAlgebraicMove("Ba6", "rn1qkb1r/p1pn1ppp/bp2p3/3pP3/3P4/3B1N2/PPPN1PPP/R1BQK2R w KQkq - 2 7")
            .testAlgebraicMove("O-O", "rn1qkb1r/p1pn1ppp/bp2p3/3pP3/3P4/3B1N2/PPPN1PPP/R1BQ1RK1 b kq - 3 7")
            .testAlgebraicMove("Bxd3", "rn1qkb1r/p1pn1ppp/1p2p3/3pP3/3P4/3b1N2/PPPN1PPP/R1BQ1RK1 w kq - 0 8")
            .testAlgebraicMove("cxd3", "rn1qkb1r/p1pn1ppp/1p2p3/3pP3/3P4/3P1N2/PP1N1PPP/R1BQ1RK1 b kq - 0 8")
            .testAlgebraicMove("Be7", "rn1qk2r/p1pnbppp/1p2p3/3pP3/3P4/3P1N2/PP1N1PPP/R1BQ1RK1 w kq - 1 9")
            .testAlgebraicMove("Nb3", "rn1qk2r/p1pnbppp/1p2p3/3pP3/3P4/1N1P1N2/PP3PPP/R1BQ1RK1 b kq - 2 9")
            .testAlgebraicMove("O-O", "rn1q1rk1/p1pnbppp/1p2p3/3pP3/3P4/1N1P1N2/PP3PPP/R1BQ1RK1 w - - 3 10")
            .testAlgebraicMove("Bd2", "rn1q1rk1/p1pnbppp/1p2p3/3pP3/3P4/1N1P1N2/PP1B1PPP/R2Q1RK1 b - - 4 10")
            .testAlgebraicMove("Na6", "r2q1rk1/p1pnbppp/np2p3/3pP3/3P4/1N1P1N2/PP1B1PPP/R2Q1RK1 w - - 5 11")
            .testAlgebraicMove("Qe2", "r2q1rk1/p1pnbppp/np2p3/3pP3/3P4/1N1P1N2/PP1BQPPP/R4RK1 b - - 6 11")
            .testAlgebraicMove("Qc8", "r1q2rk1/p1pnbppp/np2p3/3pP3/3P4/1N1P1N2/PP1BQPPP/R4RK1 w - - 7 12")
            .testAlgebraicMove("Rac1", "r1q2rk1/p1pnbppp/np2p3/3pP3/3P4/1N1P1N2/PP1BQPPP/2R2RK1 b - - 8 12")
            .testAlgebraicMove("Qb7", "r4rk1/pqpnbppp/np2p3/3pP3/3P4/1N1P1N2/PP1BQPPP/2R2RK1 w - - 9 13")
            .testAlgebraicMove("Rc2", "r4rk1/pqpnbppp/np2p3/3pP3/3P4/1N1P1N2/PPRBQPPP/5RK1 b - - 10 13")
            .testAlgebraicMove("c5", "r4rk1/pq1nbppp/np2p3/2ppP3/3P4/1N1P1N2/PPRBQPPP/5RK1 w - c6 0 14")
            .testAlgebraicMove("Rfc1", "r4rk1/pq1nbppp/np2p3/2ppP3/3P4/1N1P1N2/PPRBQPPP/2R3K1 b - - 1 14")
            .testAlgebraicMove("Rfc8", "r1r3k1/pq1nbppp/np2p3/2ppP3/3P4/1N1P1N2/PPRBQPPP/2R3K1 w - - 2 15")
            .testAlgebraicMove("dxc5", "r1r3k1/pq1nbppp/np2p3/2PpP3/8/1N1P1N2/PPRBQPPP/2R3K1 b - - 0 15")
            .testAlgebraicMove("bxc5", "r1r3k1/pq1nbppp/n3p3/2ppP3/8/1N1P1N2/PPRBQPPP/2R3K1 w - - 0 16")
            .testAlgebraicMove("Qe3", "r1r3k1/pq1nbppp/n3p3/2ppP3/8/1N1PQN2/PPRB1PPP/2R3K1 b - - 1 16")
            .testAlgebraicMove("Nb4", "r1r3k1/pq1nbppp/4p3/2ppP3/1n6/1N1PQN2/PPRB1PPP/2R3K1 w - - 2 17")
            .testAlgebraicMove("Bxb4", "r1r3k1/pq1nbppp/4p3/2ppP3/1B6/1N1PQN2/PPR2PPP/2R3K1 b - - 0 17")
            .testAlgebraicMove("Qxb4", "r1r3k1/p2nbppp/4p3/2ppP3/1q6/1N1PQN2/PPR2PPP/2R3K1 w - - 0 18")
            .testAlgebraicMove("Nbd2", "r1r3k1/p2nbppp/4p3/2ppP3/1q6/3PQN2/PPRN1PPP/2R3K1 b - - 1 18")
            .testAlgebraicMove("a5", "r1r3k1/3nbppp/4p3/p1ppP3/1q6/3PQN2/PPRN1PPP/2R3K1 w - a6 0 19")
            .testAlgebraicMove("a3", "r1r3k1/3nbppp/4p3/p1ppP3/1q6/P2PQN2/1PRN1PPP/2R3K1 b - - 0 19")
            .testAlgebraicMove("Qb7", "r1r3k1/1q1nbppp/4p3/p1ppP3/8/P2PQN2/1PRN1PPP/2R3K1 w - - 1 20")
            .testAlgebraicMove("Kh1", "r1r3k1/1q1nbppp/4p3/p1ppP3/8/P2PQN2/1PRN1PPP/2R4K b - - 2 20")
            .testAlgebraicMove("Ra6", "2r3k1/1q1nbppp/r3p3/p1ppP3/8/P2PQN2/1PRN1PPP/2R4K w - - 3 21")
            .testAlgebraicMove("d4", "2r3k1/1q1nbppp/r3p3/p1ppP3/3P4/P3QN2/1PRN1PPP/2R4K b - - 0 21")
            .testAlgebraicMove("c4", "2r3k1/1q1nbppp/r3p3/p2pP3/2pP4/P3QN2/1PRN1PPP/2R4K w - - 0 22")
            .testAlgebraicMove("Qc3", "2r3k1/1q1nbppp/r3p3/p2pP3/2pP4/P1Q2N2/1PRN1PPP/2R4K b - - 1 22")
            .testAlgebraicMove("a4", "2r3k1/1q1nbppp/r3p3/3pP3/p1pP4/P1Q2N2/1PRN1PPP/2R4K w - - 0 23")
            .testAlgebraicMove("b4", "2r3k1/1q1nbppp/r3p3/3pP3/pPpP4/P1Q2N2/2RN1PPP/2R4K b - b3 0 23")
            .testAlgebraicMove("axb3", "2r3k1/1q1nbppp/r3p3/3pP3/2pP4/PpQ2N2/2RN1PPP/2R4K w - - 0 24")
            .testAlgebraicMove("Nxb3", "2r3k1/1q1nbppp/r3p3/3pP3/2pP4/PNQ2N2/2R2PPP/2R4K b - - 0 24")
            .testAlgebraicMove("Rxa3", "2r3k1/1q1nbppp/4p3/3pP3/2pP4/rNQ2N2/2R2PPP/2R4K w - - 0 25")
            .testAlgebraicMove("Nfd2", "2r3k1/1q1nbppp/4p3/3pP3/2pP4/rNQ5/2RN1PPP/2R4K b - - 1 25")
            .testAlgebraicMove("Rb8", "1r4k1/1q1nbppp/4p3/3pP3/2pP4/rNQ5/2RN1PPP/2R4K w - - 2 26")
            .testAlgebraicMove("Rb1", "1r4k1/1q1nbppp/4p3/3pP3/2pP4/rNQ5/2RN1PPP/1R5K b - - 3 26")
            .testAlgebraicMove("cxb3", "1r4k1/1q1nbppp/4p3/3pP3/3P4/rpQ5/2RN1PPP/1R5K w - - 0 27")
            .testAlgebraicMove("Rcb2", "1r4k1/1q1nbppp/4p3/3pP3/3P4/rpQ5/1R1N1PPP/1R5K b - - 1 27")
            .testAlgebraicMove("Bb4", "1r4k1/1q1n1ppp/4p3/3pP3/1b1P4/rpQ5/1R1N1PPP/1R5K w - - 2 28")
            .testAlgebraicMove("Qc1", "1r4k1/1q1n1ppp/4p3/3pP3/1b1P4/rp6/1R1N1PPP/1RQ4K b - - 3 28")
            .testAlgebraicMove("Bxd2", "1r4k1/1q1n1ppp/4p3/3pP3/3P4/rp6/1R1b1PPP/1RQ4K w - - 0 29")
            .testAlgebraicMove("Rxd2", "1r4k1/1q1n1ppp/4p3/3pP3/3P4/rp6/3R1PPP/1RQ4K b - - 0 29")
            .testAlgebraicMove("Ra2", "1r4k1/1q1n1ppp/4p3/3pP3/3P4/1p6/r2R1PPP/1RQ4K w - - 1 30")
            .testAlgebraicMove("Rdb2", "1r4k1/1q1n1ppp/4p3/3pP3/3P4/1p6/rR3PPP/1RQ4K b - - 2 30")
            .testAlgebraicMove("Rxb2", "1r4k1/1q1n1ppp/4p3/3pP3/3P4/1p6/1r3PPP/1RQ4K w - - 0 31")
            .testAlgebraicMove("Qxb2", "1r4k1/1q1n1ppp/4p3/3pP3/3P4/1p6/1Q3PPP/1R5K b - - 0 31")
            .testAlgebraicMove("Qb4", "1r4k1/3n1ppp/4p3/3pP3/1q1P4/1p6/1Q3PPP/1R5K w - - 1 32")
            .testAlgebraicMove("f4", "1r4k1/3n1ppp/4p3/3pP3/1q1P1P2/1p6/1Q4PP/1R5K b - f3 0 32")
            .testAlgebraicMove("g6", "1r4k1/3n1p1p/4p1p1/3pP3/1q1P1P2/1p6/1Q4PP/1R5K w - - 0 33")
            .testAlgebraicMove("h3", "1r4k1/3n1p1p/4p1p1/3pP3/1q1P1P2/1p5P/1Q4P1/1R5K b - - 0 33")
            .testAlgebraicMove("Qc4", "1r4k1/3n1p1p/4p1p1/3pP3/2qP1P2/1p5P/1Q4P1/1R5K w - - 1 34")
            .testAlgebraicMove("Rc1", "1r4k1/3n1p1p/4p1p1/3pP3/2qP1P2/1p5P/1Q4P1/2R4K b - - 2 34")
            .testAlgebraicMove("Qd3", "1r4k1/3n1p1p/4p1p1/3pP3/3P1P2/1p1q3P/1Q4P1/2R4K w - - 3 35")
            .testAlgebraicMove("f5", "1r4k1/3n1p1p/4p1p1/3pPP2/3P4/1p1q3P/1Q4P1/2R4K b - - 0 35")
            .testAlgebraicMove("Qxf5", "1r4k1/3n1p1p/4p1p1/3pPq2/3P4/1p5P/1Q4P1/2R4K w - - 0 36")
    }

    // https://en.wikipedia.org/wiki/Game_of_the_Century_(chess)
    @Test
    fun fischerTest() {
        /*
        1. Nf3 Nf6 2. c4 g6 3. Nc3 Bg7 4. d4 O-O 5. Bf4 d5 6. Qb3 dxc4 7. Qxc4 c6 8. e4 Nbd7 9. Rd1 Nb6 10. Qc5 Bg4 11. Bg5 Na4
        12. Qa3 Nxc3 13. bxc3 Nxe4 14. Bxe7 Qb6 15. Bc4 Nxc3 16. Bc5 Rfe8+ 17. Kf1 Be6 18. Bxb6 Bxc4+ 19. Kg1 Ne2+ 20. Kf1 Nxd4+
        21. Kg1 Ne2+ 22. Kf1 Nc3+ 23. Kg1 axb6 24. Qb4 Ra4 25. Qxb6 Nxd1 26. h3 Rxa2 27. Kh2 Nxf2 28. Re1 Rxe1 29. Qd8+ Bf8
        30. Nxe1 Bd5 31. Nf3 Ne4 32. Qb8 b5 33. h4 h5 34. Ne5 Kg7 35. Kg1 Bc5+ 36. Kf1 Ng3+ 37. Ke1 Bb4+ 38. Kd1 Bb3+ 39. Kc1 Ne2+
        40. Kb1 Nc3+ 41. Kc1 Rc2# 0-1
         */
        BoardState.initial()
            .testAlgebraicMove("Nf3", "rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQKB1R b KQkq - 1 1")
            .testAlgebraicMove("Nf6", "rnbqkb1r/pppppppp/5n2/8/8/5N2/PPPPPPPP/RNBQKB1R w KQkq - 2 2")
            .testAlgebraicMove("c4", "rnbqkb1r/pppppppp/5n2/8/2P5/5N2/PP1PPPPP/RNBQKB1R b KQkq c3 0 2")
            .testAlgebraicMove("g6", "rnbqkb1r/pppppp1p/5np1/8/2P5/5N2/PP1PPPPP/RNBQKB1R w KQkq - 0 3")
            .testAlgebraicMove("Nc3", "rnbqkb1r/pppppp1p/5np1/8/2P5/2N2N2/PP1PPPPP/R1BQKB1R b KQkq - 1 3")
            .testAlgebraicMove("Bg7", "rnbqk2r/ppppppbp/5np1/8/2P5/2N2N2/PP1PPPPP/R1BQKB1R w KQkq - 2 4")
            .testAlgebraicMove("d4", "rnbqk2r/ppppppbp/5np1/8/2PP4/2N2N2/PP2PPPP/R1BQKB1R b KQkq d3 0 4")
            .testAlgebraicMove("O-O", "rnbq1rk1/ppppppbp/5np1/8/2PP4/2N2N2/PP2PPPP/R1BQKB1R w KQ - 1 5")
            .testAlgebraicMove("Bf4", "rnbq1rk1/ppppppbp/5np1/8/2PP1B2/2N2N2/PP2PPPP/R2QKB1R b KQ - 2 5")
            .testAlgebraicMove("d5", "rnbq1rk1/ppp1ppbp/5np1/3p4/2PP1B2/2N2N2/PP2PPPP/R2QKB1R w KQ d6 0 6")
            .testAlgebraicMove("Qb3", "rnbq1rk1/ppp1ppbp/5np1/3p4/2PP1B2/1QN2N2/PP2PPPP/R3KB1R b KQ - 1 6")
            .testAlgebraicMove("dxc4", "rnbq1rk1/ppp1ppbp/5np1/8/2pP1B2/1QN2N2/PP2PPPP/R3KB1R w KQ - 0 7")
            .testAlgebraicMove("Qxc4", "rnbq1rk1/ppp1ppbp/5np1/8/2QP1B2/2N2N2/PP2PPPP/R3KB1R b KQ - 0 7")
            .testAlgebraicMove("c6", "rnbq1rk1/pp2ppbp/2p2np1/8/2QP1B2/2N2N2/PP2PPPP/R3KB1R w KQ - 0 8")
            .testAlgebraicMove("e4", "rnbq1rk1/pp2ppbp/2p2np1/8/2QPPB2/2N2N2/PP3PPP/R3KB1R b KQ e3 0 8")
            .testAlgebraicMove("Nbd7", "r1bq1rk1/pp1nppbp/2p2np1/8/2QPPB2/2N2N2/PP3PPP/R3KB1R w KQ - 1 9")
            .testAlgebraicMove("Rd1", "r1bq1rk1/pp1nppbp/2p2np1/8/2QPPB2/2N2N2/PP3PPP/3RKB1R b K - 2 9")
            .testAlgebraicMove("Nb6", "r1bq1rk1/pp2ppbp/1np2np1/8/2QPPB2/2N2N2/PP3PPP/3RKB1R w K - 3 10")
            .testAlgebraicMove("Qc5", "r1bq1rk1/pp2ppbp/1np2np1/2Q5/3PPB2/2N2N2/PP3PPP/3RKB1R b K - 4 10")
            .testAlgebraicMove("Bg4", "r2q1rk1/pp2ppbp/1np2np1/2Q5/3PPBb1/2N2N2/PP3PPP/3RKB1R w K - 5 11")
            .testAlgebraicMove("Bg5", "r2q1rk1/pp2ppbp/1np2np1/2Q3B1/3PP1b1/2N2N2/PP3PPP/3RKB1R b K - 6 11")
            .testAlgebraicMove("Na4", "r2q1rk1/pp2ppbp/2p2np1/2Q3B1/n2PP1b1/2N2N2/PP3PPP/3RKB1R w K - 7 12")
            .testAlgebraicMove("Qa3", "r2q1rk1/pp2ppbp/2p2np1/6B1/n2PP1b1/Q1N2N2/PP3PPP/3RKB1R b K - 8 12")
            .testAlgebraicMove("Nxc3", "r2q1rk1/pp2ppbp/2p2np1/6B1/3PP1b1/Q1n2N2/PP3PPP/3RKB1R w K - 0 13")
            .testAlgebraicMove("bxc3", "r2q1rk1/pp2ppbp/2p2np1/6B1/3PP1b1/Q1P2N2/P4PPP/3RKB1R b K - 0 13")
            .testAlgebraicMove("Nxe4", "r2q1rk1/pp2ppbp/2p3p1/6B1/3Pn1b1/Q1P2N2/P4PPP/3RKB1R w K - 0 14")
            .testAlgebraicMove("Bxe7", "r2q1rk1/pp2Bpbp/2p3p1/8/3Pn1b1/Q1P2N2/P4PPP/3RKB1R b K - 0 14")
            .testAlgebraicMove("Qb6", "r4rk1/pp2Bpbp/1qp3p1/8/3Pn1b1/Q1P2N2/P4PPP/3RKB1R w K - 1 15")
            .testAlgebraicMove("Bc4", "r4rk1/pp2Bpbp/1qp3p1/8/2BPn1b1/Q1P2N2/P4PPP/3RK2R b K - 2 15")
            .testAlgebraicMove("Nxc3", "r4rk1/pp2Bpbp/1qp3p1/8/2BP2b1/Q1n2N2/P4PPP/3RK2R w K - 0 16")
            .testAlgebraicMove("Bc5", "r4rk1/pp3pbp/1qp3p1/2B5/2BP2b1/Q1n2N2/P4PPP/3RK2R b K - 1 16")
            .testAlgebraicMove("Rfe8+", "r3r1k1/pp3pbp/1qp3p1/2B5/2BP2b1/Q1n2N2/P4PPP/3RK2R w K - 2 17")
            .testAlgebraicMove("Kf1", "r3r1k1/pp3pbp/1qp3p1/2B5/2BP2b1/Q1n2N2/P4PPP/3R1K1R b - - 3 17")
            .testAlgebraicMove("Be6", "r3r1k1/pp3pbp/1qp1b1p1/2B5/2BP4/Q1n2N2/P4PPP/3R1K1R w - - 4 18")
            .testAlgebraicMove("Bxb6", "r3r1k1/pp3pbp/1Bp1b1p1/8/2BP4/Q1n2N2/P4PPP/3R1K1R b - - 0 18")
            .testAlgebraicMove("Bxc4+", "r3r1k1/pp3pbp/1Bp3p1/8/2bP4/Q1n2N2/P4PPP/3R1K1R w - - 0 19")
            .testAlgebraicMove("Kg1", "r3r1k1/pp3pbp/1Bp3p1/8/2bP4/Q1n2N2/P4PPP/3R2KR b - - 1 19")
            .testAlgebraicMove("Ne2+", "r3r1k1/pp3pbp/1Bp3p1/8/2bP4/Q4N2/P3nPPP/3R2KR w - - 2 20")
            .testAlgebraicMove("Kf1", "r3r1k1/pp3pbp/1Bp3p1/8/2bP4/Q4N2/P3nPPP/3R1K1R b - - 3 20")
            .testAlgebraicMove("Nxd4+", "r3r1k1/pp3pbp/1Bp3p1/8/2bn4/Q4N2/P4PPP/3R1K1R w - - 0 21")
            .testAlgebraicMove("Kg1", "r3r1k1/pp3pbp/1Bp3p1/8/2bn4/Q4N2/P4PPP/3R2KR b - - 1 21")
            .testAlgebraicMove("Ne2+", "r3r1k1/pp3pbp/1Bp3p1/8/2b5/Q4N2/P3nPPP/3R2KR w - - 2 22")
            .testAlgebraicMove("Kf1", "r3r1k1/pp3pbp/1Bp3p1/8/2b5/Q4N2/P3nPPP/3R1K1R b - - 3 22")
            .testAlgebraicMove("Nc3+", "r3r1k1/pp3pbp/1Bp3p1/8/2b5/Q1n2N2/P4PPP/3R1K1R w - - 4 23")
            .testAlgebraicMove("Kg1", "r3r1k1/pp3pbp/1Bp3p1/8/2b5/Q1n2N2/P4PPP/3R2KR b - - 5 23")
            .testAlgebraicMove("axb6", "r3r1k1/1p3pbp/1pp3p1/8/2b5/Q1n2N2/P4PPP/3R2KR w - - 0 24")
            .testAlgebraicMove("Qb4", "r3r1k1/1p3pbp/1pp3p1/8/1Qb5/2n2N2/P4PPP/3R2KR b - - 1 24")
            .testAlgebraicMove("Ra4", "4r1k1/1p3pbp/1pp3p1/8/rQb5/2n2N2/P4PPP/3R2KR w - - 2 25")
            .testAlgebraicMove("Qxb6", "4r1k1/1p3pbp/1Qp3p1/8/r1b5/2n2N2/P4PPP/3R2KR b - - 0 25")
            .testAlgebraicMove("Nxd1", "4r1k1/1p3pbp/1Qp3p1/8/r1b5/5N2/P4PPP/3n2KR w - - 0 26")
            .testAlgebraicMove("h3", "4r1k1/1p3pbp/1Qp3p1/8/r1b5/5N1P/P4PP1/3n2KR b - - 0 26")
            .testAlgebraicMove("Rxa2", "4r1k1/1p3pbp/1Qp3p1/8/2b5/5N1P/r4PP1/3n2KR w - - 0 27")
            .testAlgebraicMove("Kh2", "4r1k1/1p3pbp/1Qp3p1/8/2b5/5N1P/r4PPK/3n3R b - - 1 27")
            .testAlgebraicMove("Nxf2", "4r1k1/1p3pbp/1Qp3p1/8/2b5/5N1P/r4nPK/7R w - - 0 28")
            .testAlgebraicMove("Re1", "4r1k1/1p3pbp/1Qp3p1/8/2b5/5N1P/r4nPK/4R3 b - - 1 28")
            .testAlgebraicMove("Rxe1", "6k1/1p3pbp/1Qp3p1/8/2b5/5N1P/r4nPK/4r3 w - - 0 29")
            .testAlgebraicMove("Qd8+", "3Q2k1/1p3pbp/2p3p1/8/2b5/5N1P/r4nPK/4r3 b - - 1 29")
            .testAlgebraicMove("Bf8", "3Q1bk1/1p3p1p/2p3p1/8/2b5/5N1P/r4nPK/4r3 w - - 2 30")
            .testAlgebraicMove("Nxe1", "3Q1bk1/1p3p1p/2p3p1/8/2b5/7P/r4nPK/4N3 b - - 0 30")
            .testAlgebraicMove("Bd5", "3Q1bk1/1p3p1p/2p3p1/3b4/8/7P/r4nPK/4N3 w - - 1 31")
            .testAlgebraicMove("Nf3", "3Q1bk1/1p3p1p/2p3p1/3b4/8/5N1P/r4nPK/8 b - - 2 31")
            .testAlgebraicMove("Ne4", "3Q1bk1/1p3p1p/2p3p1/3b4/4n3/5N1P/r5PK/8 w - - 3 32")
            .testAlgebraicMove("Qb8", "1Q3bk1/1p3p1p/2p3p1/3b4/4n3/5N1P/r5PK/8 b - - 4 32")
            .testAlgebraicMove("b5", "1Q3bk1/5p1p/2p3p1/1p1b4/4n3/5N1P/r5PK/8 w - b6 0 33")
            .testAlgebraicMove("h4", "1Q3bk1/5p1p/2p3p1/1p1b4/4n2P/5N2/r5PK/8 b - - 0 33")
            .testAlgebraicMove("h5", "1Q3bk1/5p2/2p3p1/1p1b3p/4n2P/5N2/r5PK/8 w - h6 0 34")
            .testAlgebraicMove("Ne5", "1Q3bk1/5p2/2p3p1/1p1bN2p/4n2P/8/r5PK/8 b - - 1 34")
            .testAlgebraicMove("Kg7", "1Q3b2/5pk1/2p3p1/1p1bN2p/4n2P/8/r5PK/8 w - - 2 35")
            .testAlgebraicMove("Kg1", "1Q3b2/5pk1/2p3p1/1p1bN2p/4n2P/8/r5P1/6K1 b - - 3 35")
            .testAlgebraicMove("Bc5+", "1Q6/5pk1/2p3p1/1pbbN2p/4n2P/8/r5P1/6K1 w - - 4 36")
            .testAlgebraicMove("Kf1", "1Q6/5pk1/2p3p1/1pbbN2p/4n2P/8/r5P1/5K2 b - - 5 36")
            .testAlgebraicMove("Ng3+", "1Q6/5pk1/2p3p1/1pbbN2p/7P/6n1/r5P1/5K2 w - - 6 37")
            .testAlgebraicMove("Ke1", "1Q6/5pk1/2p3p1/1pbbN2p/7P/6n1/r5P1/4K3 b - - 7 37")
            .testAlgebraicMove("Bb4+", "1Q6/5pk1/2p3p1/1p1bN2p/1b5P/6n1/r5P1/4K3 w - - 8 38")
            .testAlgebraicMove("Kd1", "1Q6/5pk1/2p3p1/1p1bN2p/1b5P/6n1/r5P1/3K4 b - - 9 38")
            .testAlgebraicMove("Bb3+", "1Q6/5pk1/2p3p1/1p2N2p/1b5P/1b4n1/r5P1/3K4 w - - 10 39")
            .testAlgebraicMove("Kc1", "1Q6/5pk1/2p3p1/1p2N2p/1b5P/1b4n1/r5P1/2K5 b - - 11 39")
            .testAlgebraicMove("Ne2+", "1Q6/5pk1/2p3p1/1p2N2p/1b5P/1b6/r3n1P1/2K5 w - - 12 40")
            .testAlgebraicMove("Kb1", "1Q6/5pk1/2p3p1/1p2N2p/1b5P/1b6/r3n1P1/1K6 b - - 13 40")
            .testAlgebraicMove("Nc3+", "1Q6/5pk1/2p3p1/1p2N2p/1b5P/1bn5/r5P1/1K6 w - - 14 41")
            .testAlgebraicMove("Kc1", "1Q6/5pk1/2p3p1/1p2N2p/1b5P/1bn5/r5P1/2K5 b - - 15 41")
            .testAlgebraicMove("Rc2#", "1Q6/5pk1/2p3p1/1p2N2p/1b5P/1bn5/2r3P1/2K5 w - - 16 42")
    }

    @Test
    fun longCastleTest() {
        BoardState.fromFen("r1bk3r/1ppn1pp1/p6p/8/1bP5/2N1P3/PP1B1PPP/R3KB1R w KQ - 0 11").applyStandardAlgebraicMove("O-O-O")
    }

    @Test
    fun checkmateTest() {
        var myState = BoardState.fromFen("3rk2r/1p3ppp/p1qQbn2/4N3/4P3/2N1B3/PP3PPP/2KR3R w k - 1 17").applyStandardAlgebraicMove("Qxd8#")
    }

    @Test
    fun checkPromotionTest() {
        BoardState.fromFen("8/8/6p1/7p/4p2P/4kp2/3p4/6K1 b - - 1 51").applyStandardAlgebraicMove("d1=Q+")
    }

    @Test
    fun castlingValidityCheck() {
        Assert.assertEquals(
            "r2qkbnr/p2n1ppp/8/1B2p3/8/2N5/PPPPQP1P/R1B1K1Nb w Qkq - 0 8",
            BoardState.fromFen("r2qkbnr/p2n1ppp/8/1B2p3/8/2N5/PPPPQPbP/R1B1K1NR b KQkq - 1 7").applyStandardAlgebraicMove("Bxh1").toFenString()
        )
        Assert.assertEquals(
            "4k2r/5ppp/2p5/1pbp4/4pP2/6Pb/1PP1B2P/rq1R3K w k - 3 29",
            BoardState.fromFen("r3k2r/5ppp/2p5/1pbp4/4pP2/6Pb/1PP1B2P/1q1R3K b kq - 2 28").applyStandardAlgebraicMove("Ra1").toFenString()
        )
    }
}
