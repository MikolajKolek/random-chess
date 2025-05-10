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
    fun enPassantTest() {
        //https://www.chessprogramming.org/En_passant#En_passant_bugs
        val board = BoardState.fromFen(FEN("2r3k1/1q1nbppp/r3p3/3pP3/pPpP4/P1Q2N2/2RN1PPP/2R4K b - b3 0 23"))

        Assert.assertTrue(board.getLegalMovesFor(Square(3, 0))
            .contains(Move(Square(3, 0), Square(2, 1)))
        )
        Assert.assertTrue(board.getLegalMovesFor(Square(3, 2))
            .contains(Move(Square(3, 2), Square(2, 1)))
        )
    }
}