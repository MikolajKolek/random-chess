package pl.edu.uj.tcs.rchess.model

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class SquareTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun squareArgsInRange(){
        Assert.assertThrows(IllegalArgumentException::class.java) {
            Square(7, 8, null)
        }
    }
}