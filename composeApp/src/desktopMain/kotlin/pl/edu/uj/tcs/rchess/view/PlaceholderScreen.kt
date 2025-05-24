package pl.edu.uj.tcs.rchess.view

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PlaceholderScreen(modifier: Modifier = Modifier, text: String) {
    Box(modifier = modifier) {
        Text(text)
    }
}
