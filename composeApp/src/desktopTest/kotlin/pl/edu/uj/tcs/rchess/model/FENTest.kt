package pl.edu.uj.tcs.rchess.model

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class FENTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun defaultConstructorShouldWork(){
        FEN()
    }

    @Test
    fun variousValidFENTest() {
        val fens = listOf(
            "r1bk3r/p2pBpNp/n4n2/1p1NP2P/6P1/3P4/P1P1K3/q5b1 b - - 1 23",
            "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
            "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2",
            "3q3k/p1p3Q1/1p5p/8/4p3/1PP3RP/P4PPK/3r4 b - - 2 32",
            "r2qk2r/pp3ppp/2nbpn2/2pp3b/8/1P1P1NPP/PBPNPPB1/R2Q1RK1 b kq - 2 9"
        )

        for(fen in fens)
            Assert.assertEquals(fen, BoardState.fromFen(FEN(fen)).toFenString())
    }

    @Test
    fun invalidBoardTest() {
        Assert.assertThrows(IllegalArgumentException::class.java) { FEN("r1bk2r/p2pBpNp/n4n2/1p1NP2P/6P1/3P4/P1P1K3/q5b1 b - - 1 23") }
        Assert.assertThrows(IllegalArgumentException::class.java) { FEN("8/8/8/8 b - - 0 1") }
        Assert.assertThrows(IllegalArgumentException::class.java) { FEN("8/8/8/8/7/8/8/8 b - - 0 23") }
        Assert.assertThrows(IllegalArgumentException::class.java) { FEN("/////// b - - 0 23") }
    }

    @Test
    fun wrongArgumentNumberTest() {
        Assert.assertThrows(IllegalArgumentException::class.java) { FEN("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0") }
        Assert.assertThrows(IllegalArgumentException::class.java) { FEN("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3") }
        Assert.assertThrows(IllegalArgumentException::class.java) { FEN("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq") }
        Assert.assertThrows(IllegalArgumentException::class.java) { FEN("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b") }
        Assert.assertThrows(IllegalArgumentException::class.java) { FEN("b KQkq e3 0 1") }
        Assert.assertThrows(IllegalArgumentException::class.java) { FEN("KQkq") }
    }

    @Test
    fun enPassantSquareTest() {
        FEN("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2")
        Assert.assertThrows(IllegalArgumentException::class.java) { FEN("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c9 0 2") }
        Assert.assertThrows(IllegalArgumentException::class.java) { FEN("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c4 0 2") }
    }

    @Test
    fun invalidPositionTest() {
        Assert.assertThrows(IllegalArgumentException::class.java) { FEN("Kk6/8/8/8/8/8/8/8 w - - 0 1") }
    }
}