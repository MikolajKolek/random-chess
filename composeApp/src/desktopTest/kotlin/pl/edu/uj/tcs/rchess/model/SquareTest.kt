package pl.edu.uj.tcs.rchess.model

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class SquareTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun squareArgsInRange() {
        for ((row, col) in listOf(
            0 to 8,
            8 to 0,
            -1 to 0,
            0 to -1
        )) {
            Assert.assertThrows(IllegalArgumentException::class.java) {
                Square(row, col)
            }
        }
    }

    @Test
    fun fromStringTest() {
        Assert.assertEquals(Square(0, 2), Square.fromString("c1"))
        Assert.assertEquals(Square(3, 4), Square.fromString("e4"))
    }

    @Test
    fun toStringTest() {
        Assert.assertEquals("c1", Square(0, 2).toString())
        Assert.assertEquals("e4", Square(3, 4).toString())
    }
}
