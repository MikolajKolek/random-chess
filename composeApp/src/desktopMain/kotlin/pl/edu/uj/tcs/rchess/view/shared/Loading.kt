package pl.edu.uj.tcs.rchess.view.shared

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun Loading(modifier: Modifier = Modifier, text: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        // TODO: Style with Material 3 Expressive
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
        )

        Text(
            text,
            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
        )
    }
}
