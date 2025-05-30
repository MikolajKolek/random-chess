package pl.edu.uj.tcs.rchess.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import formatReason
import formatResult
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.server.game.HistoryGame
import pl.edu.uj.tcs.rchess.server.game.HistoryServiceGame
import pl.edu.uj.tcs.rchess.server.game.PgnGame
import pl.edu.uj.tcs.rchess.view.board.BoardView
import pl.edu.uj.tcs.rchess.view.shared.PlayerName
import pl.edu.uj.tcs.rchess.view.shared.ServiceLabel
import java.time.format.DateTimeFormatter

@Composable
fun GameHistoryItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    game: HistoryGame,
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min).fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BoardView(
                16.dp,
                state = game.finalPosition,
                orientation = PlayerColor.WHITE,
            )

            Column(
                modifier = Modifier.fillMaxHeight(),
            ) {
                PlayerName(Modifier.weight(1f), game.getPlayerName(PlayerColor.BLACK), PlayerColor.BLACK)
                Column(
                    modifier = Modifier.weight(2f).padding(start = 8.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(game.result.formatResult(), style = MaterialTheme.typography.bodyMedium)
                    Text(game.result.formatReason(), style = MaterialTheme.typography.bodySmall)
                }
                PlayerName(Modifier.weight(1f), game.getPlayerName(PlayerColor.WHITE), PlayerColor.WHITE)
            }

            Column(
                modifier = Modifier.fillMaxHeight().padding(all = 8.dp).weight(1f),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    buildString {
                        when (game) {
                            is PgnGame -> append("Imported at ")
                            is HistoryServiceGame -> append("Played on ")
                        }
                        append(game.creationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    },
                    style = MaterialTheme.typography.bodySmall
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (game) {
                        is PgnGame -> {
                            Text("Manually imported as #${game.id}")
                        }

                        is HistoryServiceGame -> {
                            Text("Played on ")
                            ServiceLabel(game.service)
                        }
                    }
                }
            }
        }
    }
}
