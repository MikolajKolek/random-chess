package pl.edu.uj.tcs.rchess.model

import org.junit.Assert
import org.junit.Test
import pl.edu.uj.tcs.rchess.model.state.BoardState

class ShortAlgebraicTest {
    @Test
    fun simpleTest() {
        Assert.assertEquals(Move(Square.fromString("b1"), Square.fromString("c3")), BoardState.initial.standardAlgebraicToMove("Nc3"))
        Assert.assertEquals(Move(Square.fromString("a2"), Square.fromString("a4")), BoardState.initial.standardAlgebraicToMove("a4"))
    }

    @Test
    fun simpleConversionTest() {
        Assert.assertEquals("Nc3", BoardState.initial.moveToStandardAlgebraic(BoardState.initial.standardAlgebraicToMove("Nc3")))
    }
}