package pl.edu.uj.tcs.rchess.model

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import pl.edu.uj.tcs.rchess.model.Fen.Companion.fromFen
import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString
import pl.edu.uj.tcs.rchess.model.state.BoardState

class FenTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun variousValidFENTest() {
        val fens = listOf(
            Fen.INITIAL,
            "r1bk3r/p2pBpNp/n4n2/1p1NP2P/6P1/3P4/P1P1K3/q5b1 b - - 1 23",
            "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
            "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2",
            "3q3k/p1p3Q1/1p5p/8/4p3/1PP3RP/P4PPK/3r4 b - - 2 32",
            "r2qk2r/pp3ppp/2nbpn2/2pp3b/8/1P1P1NPP/PBPNPPB1/R2Q1RK1 b kq - 2 9",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
            "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2",
            "8/5pp1/4n1kp/P7/7P/1P3PP1/3R4/4R1K1 b - - 0 45",
            "4n1k1/5pp1/3p3p/p3p3/Pn2P2P/1P2BPP1/8/1R3RK1 w - - 1 34",
            "4rrk1/ppp3pp/5n2/8/4PP2/2N1K3/PP5P/R6R b - - 0 20",
            "8/1pp1R1pk/p1r4p/8/3K4/1P6/P6P/8 b - - 1 32",
            "3q3K/4r3/7R/8/1p6/7p/5k2/8 w - - 12 64",
            "rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2",
            "r1bqkbnr/pppp1ppp/2n5/8/3NP3/8/PPP2PPP/RNBQKB1R b KQkq - 0 4",
            "8/p4R2/1pkp3p/1Bp5/2P5/6B1/PP2r2P/7K b - - 2 31"
        )

        for (fen in fens)
            Assert.assertEquals(fen, BoardState.fromFen(fen).toFenString())
    }

    @Test
    fun invalidBoardTest() {
        Assert.assertThrows(IllegalArgumentException::class.java) { BoardState.fromFen("r1bk2r/p2pBpNp/n4n2/1p1NP2P/6P1/3P4/P1P1K3/q5b1 b - - 1 23") }
        Assert.assertThrows(IllegalArgumentException::class.java) { BoardState.fromFen("8/8/8/8 b - - 0 1") }
        Assert.assertThrows(IllegalArgumentException::class.java) { BoardState.fromFen("8/8/8/8/7/8/8/8 b - - 0 23") }
        Assert.assertThrows(IllegalArgumentException::class.java) { BoardState.fromFen("/////// b - - 0 23") }
    }

    @Test
    fun wrongArgumentNumberTest() {
        Assert.assertThrows(IllegalArgumentException::class.java) { BoardState.fromFen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0") }
        Assert.assertThrows(IllegalArgumentException::class.java) { BoardState.fromFen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3") }
        Assert.assertThrows(IllegalArgumentException::class.java) { BoardState.fromFen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq") }
        Assert.assertThrows(IllegalArgumentException::class.java) { BoardState.fromFen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b") }
        Assert.assertThrows(IllegalArgumentException::class.java) { BoardState.fromFen("b KQkq e3 0 1") }
        Assert.assertThrows(IllegalArgumentException::class.java) { BoardState.fromFen("KQkq") }
    }

    @Test
    fun enPassantSquareTest() {
        BoardState.fromFen("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2")
        Assert.assertThrows(IllegalArgumentException::class.java) { BoardState.fromFen("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c9 0 2") }
        Assert.assertThrows(IllegalArgumentException::class.java) { BoardState.fromFen("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c4 0 2") }
    }

    @Test
    fun invalidPositionTest() {
        Assert.assertThrows(IllegalArgumentException::class.java) { BoardState.fromFen("Kk6/8/8/8/8/8/8/8 w - - 0 1") }
    }
}
