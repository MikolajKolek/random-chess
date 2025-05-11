package pl.edu.uj.tcs.rchess.model

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import pl.edu.uj.tcs.rchess.model.pieces.*

class BoardStateTest {
    @get:Rule
    val rule = createComposeRule()

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
    //https://www.chessprogramming.org/En_passant#En_passant_bugs
    fun enPassantTest() {
        val fenString = "2r3k1/1q1nbppp/r3p3/3pP3/pPpP4/P1Q2N2/2RN1PPP/2R4K b - b3 0 23"
        var board = BoardState.fromFen(FEN(fenString))

        Assert.assertTrue(board.getLegalMovesFor(Square(3, 0))
            .contains(Move(Square(3, 0), Square(2, 1)))
        )
        Assert.assertTrue(board.getLegalMovesFor(Square(3, 2))
            .contains(Move(Square(3, 2), Square(2, 1)))
        )

        board = BoardState.empty()
            .applyStandardAlgebraicMove("e4")
            .applyStandardAlgebraicMove("e6")
            .applyStandardAlgebraicMove("d4")
            .applyStandardAlgebraicMove("d5")
            .applyStandardAlgebraicMove("Nd2")
            .applyStandardAlgebraicMove("Nf6")
            .applyStandardAlgebraicMove("e5")
            .applyStandardAlgebraicMove("Nfd7")
            .applyStandardAlgebraicMove("Bd3")
            .applyStandardAlgebraicMove("b6")
            .applyStandardAlgebraicMove("Ngf3")
            .applyStandardAlgebraicMove("Ba6")
            .applyStandardAlgebraicMove("O-O")
            .applyStandardAlgebraicMove("Bxd3")
            .applyStandardAlgebraicMove("cxd3")
            .applyStandardAlgebraicMove("Be7")
            .applyStandardAlgebraicMove("Nb3")
            .applyStandardAlgebraicMove("O-O")
            .applyStandardAlgebraicMove("Bd2")
            .applyStandardAlgebraicMove("Na6")
            .applyStandardAlgebraicMove("Qe2")
            .applyStandardAlgebraicMove("Qc8")
            .applyStandardAlgebraicMove("Rac1")
            .applyStandardAlgebraicMove("Qb7")
            .applyStandardAlgebraicMove("Rc2")
            .applyStandardAlgebraicMove("c5")
            .applyStandardAlgebraicMove("Rfc1")
            .applyStandardAlgebraicMove("Rfc8")
            .applyStandardAlgebraicMove("dxc5")
            .applyStandardAlgebraicMove("bxc5")
            .applyStandardAlgebraicMove("Qe3")
            .applyStandardAlgebraicMove("nb4")
            .applyStandardAlgebraicMove("Bxb4")
            .applyStandardAlgebraicMove("Qxb4")
            .applyStandardAlgebraicMove("Nbd2")
            .applyStandardAlgebraicMove("a5")
            .applyStandardAlgebraicMove("a3")
            .applyStandardAlgebraicMove("Qb7")
            .applyStandardAlgebraicMove("Kh1")
            .applyStandardAlgebraicMove("Ra6")
            .applyStandardAlgebraicMove("d4")
            .applyStandardAlgebraicMove("c4")
            .applyStandardAlgebraicMove("Qc3")
            .applyStandardAlgebraicMove("a4")
            .applyStandardAlgebraicMove("b4")

        Assert.assertEquals(board.toFenString(), fenString)
    }
}