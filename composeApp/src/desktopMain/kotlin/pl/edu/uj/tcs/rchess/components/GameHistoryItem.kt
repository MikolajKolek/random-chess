package pl.edu.uj.tcs.rchess.components

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import pl.edu.uj.tcs.rchess.server.HistoryGame
import pl.edu.uj.tcs.rchess.server.PgnGame
import pl.edu.uj.tcs.rchess.server.ServiceGame

@Composable
fun GameHistoryItem(game: HistoryGame) {
    ListItem(
        headlineContent = {
            when (game) {
                is PgnGame -> {
                    Text("White player: ${game.whitePlayerName}")
                    Text("Black player: ${game.blackPlayerName}")
                }
                is ServiceGame -> {
                    Text("White player: ${game.whitePlayer.displayName}")
                    Text("Black player: ${game.blackPlayer.displayName}")
                }
            }
        },
        supportingContent = {
            Text("Date: ${game.creationDate}")
            Text("Game ID: ${game.id}")
        },
    )
}
