package pl.edu.uj.tcs.rchess.model

import org.junit.Assert
import org.junit.Test
import pl.edu.uj.tcs.rchess.model.Fen.Companion.fromFen
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

    @Test
    fun threeQueensTest() {
        Assert.assertEquals("Qc2f5", BoardState.fromFen("8/8/k7/2Q5/8/8/2Q2Q2/K7 w - - 0 1")
            .moveToStandardAlgebraic(Move.fromLongAlgebraicNotation("c2f5"))
        )
        Assert.assertEquals("c2f5", BoardState.fromFen("8/8/k7/2Q5/8/8/2Q2Q2/K7 w - - 0 1")
            .standardAlgebraicToMove("Qc2f5").toLongAlgebraicNotation()
        )
    }
}
