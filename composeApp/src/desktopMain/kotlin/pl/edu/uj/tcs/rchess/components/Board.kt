package pl.edu.uj.tcs.rchess.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun Board() {
    val isBlack = true; // TODO: Replace with enum from model
    val rows = when (isBlack) {
        false -> 7 downTo 0
        true -> 0..7
    }
    val columns = when (isBlack) {
        false -> 0..7
        true -> 7 downTo 0
    }

    val pieces: List<Triple<Int, Int, String>> = listOf(
        Triple(0, 0, "R"),
        Triple(0, 1, "N"),
        Triple(1, 2, "B"),
        Triple(2, 3, "Q"),
        Triple(0, 4, "K"),
        Triple(4, 5, "B"),
        Triple(0, 6, "N"),
        Triple(6, 7, "R"),
    )

    Column(
        modifier = Modifier.width(512.dp).height(512.dp).border(2.dp, Color.Black),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        for (row in rows) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                for (column in columns) {
                    // TODO: Also replace with enum
                    val squareIsBlack = (row + column) % 2 == 0
                    Box(
                        modifier = Modifier
                            .weight(1f) // Ensure the square fills its grid spot equally
                            .aspectRatio(1f) // Maintain a square aspect ratio
                            .let {
                                if (squareIsBlack) {
                                    it.background(Color.LightGray)
                                } else {
                                    it
                                }
                            }
                    ) {
                        Text("$row $column")
                    }
                }
            }
        }
    }
}
