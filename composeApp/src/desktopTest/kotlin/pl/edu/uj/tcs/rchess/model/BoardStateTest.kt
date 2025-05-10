package pl.edu.uj.tcs.rchess.model

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

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
    }
}