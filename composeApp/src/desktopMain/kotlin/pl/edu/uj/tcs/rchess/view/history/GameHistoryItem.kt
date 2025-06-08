package pl.edu.uj.tcs.rchess.view.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryGame
import pl.edu.uj.tcs.rchess.api.entity.game.HistoryServiceGame
import pl.edu.uj.tcs.rchess.api.entity.game.PgnGame
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.view.board.BoardView
import pl.edu.uj.tcs.rchess.view.shared.OpeningInfo
import pl.edu.uj.tcs.rchess.view.shared.PlayerName
import pl.edu.uj.tcs.rchess.view.shared.format
import pl.edu.uj.tcs.rchess.view.shared.formatReason
import pl.edu.uj.tcs.rchess.view.shared.formatResult
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
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
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
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
                squareBorder = true,
            )

            Column(
                modifier = Modifier.fillMaxHeight(),
            ) {
                PlayerName(Modifier.weight(1f), game.blackPlayer, PlayerColor.BLACK)
                Column(
                    modifier = Modifier.weight(2f).padding(start = 8.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(game.result.formatResult(), style = MaterialTheme.typography.bodyMedium)
                    Text(game.result.formatReason(), style = MaterialTheme.typography.bodySmall)
                }
                PlayerName(Modifier.weight(1f), game.whitePlayer, PlayerColor.WHITE)
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
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.End,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (game) {
                        is PgnGame -> {
                            Text(
                                "Manually imported as #${game.id}",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.End,
                            )
                        }

                        is HistoryServiceGame -> {
                            Text(
                                "in ${game.service.format()}",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.End,
                            )
//                            ServiceLabel(game.service)
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                game.opening?.let {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                        tooltip = { RichTooltip(
                            title = {
                                Text(
                                    "Opening",
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            },
                            colors = TooltipDefaults.richTooltipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            ),
                        ) {
                            OpeningInfo(
                                modifier = Modifier.width(200.dp),
                                opening = it,
                                squareBoard = true,
                            )
                        } },
                        state = rememberTooltipState(
                            isPersistent = true,
                        ),
                    ) {
                        Text(
                            "${it.eco}: ${it.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.End,
                        )
                    }
                }
            }
        }
    }
}
