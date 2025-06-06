package pl.edu.uj.tcs.rchess.view.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.api.entity.PlayerDetails
import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.pieces.King
import pl.edu.uj.tcs.rchess.view.board.icon
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.icon_robot

@Composable
fun PlayerName(
    modifier: Modifier = Modifier,
    player: PlayerDetails,
    color: PlayerColor,
    isWinner: Boolean = false,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.padding(end = 12.dp).size(24.dp),
            painter = painterResource(King(color).icon),
            contentDescription = null,
        )

        Text(player.displayName)

        if ((player as? ServiceAccount)?.isBot ?: false) {
            Icon(
                painter = painterResource(Res.drawable.icon_robot),
                contentDescription = "Bot",
                modifier = Modifier.padding(start = 8.dp).size(16.dp),
            )
        }

        if (isWinner) {
            Text(
                text = "winner",
                modifier = Modifier.padding(start = 16.dp),
                fontWeight = Bold,
            )
        }
    }
}
