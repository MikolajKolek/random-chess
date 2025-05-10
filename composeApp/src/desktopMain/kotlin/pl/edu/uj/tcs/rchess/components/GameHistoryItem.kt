package pl.edu.uj.tcs.rchess.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.server.HistoryGame
import pl.edu.uj.tcs.rchess.server.PgnGame
import pl.edu.uj.tcs.rchess.server.ServiceGame

@Composable
fun GameHistoryItem(
    modifier: Modifier = Modifier,
    game: HistoryGame,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Card {
        Row(
            modifier =
                modifier
                    .clickable(
                        onClick = {},
                        interactionSource = interactionSource,
                        indication = ripple(),
                    )
                    .height(IntrinsicSize.Min),
        ) {
            Box(
                modifier = Modifier
                    .padding(12.dp),
            ) {
                // TODO: Replace with board preview
                Box(
                    modifier = Modifier.height(128.dp).aspectRatio(1f).background(Color.Cyan),
                )
            }

            Column(
                modifier = Modifier.padding(12.dp).fillMaxHeight().weight(1f),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row {
                    when (game) {
                        is PgnGame -> Text("Game #${game.id}")
                        is ServiceGame -> Text("Online game")
                    }
                }

                Row {
                    when (game) {
                        is PgnGame -> {
                            Text("White player: ${game.whitePlayerName}")
                            Text(" vs ")
                            Text("Black player: ${game.blackPlayerName}")
                        }

                        is ServiceGame -> {
                            Text("White player: ${game.whitePlayer.displayName}")
                            Text(" vs ")
                            Text("Black player: ${game.blackPlayer.displayName}")
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (game) {
                        is PgnGame -> {
                            Text("Manually imported")
                        }

                        is ServiceGame -> {
                            Text("Played on ")
                            ServiceLabel(game.service)
                        }
                    }

                    Text(" at ${game.creationDate}")
                }
            }
        }
    }
}
