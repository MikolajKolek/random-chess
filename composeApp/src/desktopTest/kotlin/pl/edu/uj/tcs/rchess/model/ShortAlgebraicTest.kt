package pl.edu.uj.tcs.rchess.model

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class ShortAlgebraicTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun simpleTest() {
        Assert.assertEquals(Move(Square.fromString("b1"), Square.fromString("c3")), BoardState.initial().standardAlgebraicToMove("Nc3"))
        Assert.assertEquals(Move(Square.fromString("a2"), Square.fromString("a4")), BoardState.initial().standardAlgebraicToMove("a4"))
    }

    @Test
    fun simpleConversionTest() {
        Assert.assertEquals("Nc3", BoardState.initial().movetoStandardAlgebraic(BoardState.initial().standardAlgebraicToMove("Nc3")))
    }
}