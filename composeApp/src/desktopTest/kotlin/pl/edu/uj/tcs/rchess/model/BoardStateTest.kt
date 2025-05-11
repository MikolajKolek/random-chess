package pl.edu.uj.tcs.rchess.model

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import pl.edu.uj.tcs.rchess.model.pieces.King

class BoardStateTest {
    @get:Rule
    val rule = createComposeRule()

    companion object {
        fun BoardState.testAlgebraicMove(move: String, expectedFen: String): BoardState =
            this.applyStandardAlgebraicMove(move).also {
                Assert.assertEquals(expectedFen, it.toFenString())
            }
    }

    @Test
    fun emptyPositionTest() {
        BoardState.empty()
        Assert.assertThrows(IllegalArgumentException::class.java) { BoardState.empty().isLegal() }
    }

    @Test
    fun initialPositionTest() {
        BoardState.fromFen(FEN())
        BoardState.initial().toFenString()
        Assert.assertEquals(BoardState.initial().getPieceAt(Square.fromString("e1"))!!.javaClass, King(PlayerColor.WHITE).javaClass)
    }

    @Test
    fun enPassantTest1() {
        val fenString = "2r3k1/1q1nbppp/r3p3/3pP3/pPpP4/P1Q2N2/2RN1PPP/2R4K b - b3 0 23"
        val board = BoardState.fromFen(FEN(fenString))

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
            .testAlgebraicMove("nb4", "r1r3k1/pq1nbppp/4p3/2ppP3/1n6/1N1PQN2/PPRB1PPP/2R3K1 w - - 2 17")
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
}
