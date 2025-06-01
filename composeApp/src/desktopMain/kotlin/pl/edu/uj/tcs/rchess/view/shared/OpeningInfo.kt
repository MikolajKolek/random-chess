package pl.edu.uj.tcs.rchess.view.shared

import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.server.Opening
import pl.edu.uj.tcs.rchess.view.board.BoardView

@Composable
fun OpeningInfo(
    modifier: Modifier = Modifier,
    opening: Opening,
    orientation: PlayerColor = PlayerColor.WHITE,
) {
    val openingUrl = "https://www.365chess.com/eco/${opening.eco}"

    Column(
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = buildAnnotatedString {
                withLink(LinkAnnotation.Url(url = openingUrl)) {
                    append(opening.eco)
                }
                append(": ")
                append(opening.name)
            }
        )

        OutlinedCard(
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
            ) {
                BoardView(
                    pieceSize = minWidth / 8,
                    state = opening.position,
                    orientation = orientation,
                )
            }
        }
    }
}
