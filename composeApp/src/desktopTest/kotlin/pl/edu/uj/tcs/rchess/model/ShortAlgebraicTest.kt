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
        Assert.assertEquals(Move(Square.fromString("b1"), Square.fromString("c3")), BoardState.initial().SAtoMove("Nc3"))
        Assert.assertEquals(Move(Square.fromString("e2"), Square.fromString("e4")), BoardState.initial().SAtoMove("e4"))
    }
}