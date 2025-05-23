package pl.edu.uj.tcs.rchess.view.board

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.model.PlayerColor

/**
 * @param isSelf
 */
@Composable
fun PlayerBar(
    modifier: Modifier = Modifier,
    color: PlayerColor,
    name: String?,
    isSelf: Boolean,
) {
    Row(
        modifier = modifier.height(PlayerBar.height)
    ) {
        Text(color.unicodeSymbol)

        Text(name ?: when (color) {
            PlayerColor.WHITE -> "White player"
            PlayerColor.BLACK -> "Black player"
        })

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Text("timer")
    }
}

object PlayerBar {
    val height = 24.dp
}
