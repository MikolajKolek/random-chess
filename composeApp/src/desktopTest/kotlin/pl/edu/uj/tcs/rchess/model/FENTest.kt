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
}