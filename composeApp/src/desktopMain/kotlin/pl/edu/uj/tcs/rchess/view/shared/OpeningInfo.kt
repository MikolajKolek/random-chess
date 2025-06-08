package pl.edu.uj.tcs.rchess.view.shared

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.api.entity.Opening
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.view.board.BoardView

@Composable
fun OpeningInfo(
    modifier: Modifier = Modifier,
    opening: Opening,
    orientation: PlayerColor = PlayerColor.WHITE,
    squareBoard: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = buildAnnotatedString {
                withLink(LinkAnnotation.Url(url = opening.url)) {
                    append(opening.eco)
                }
                append(": ")
                append(opening.name)
            },
            style = MaterialTheme.typography.bodyMedium,
        )

        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        ) {
            BoardView(
                pieceSize = minWidth / 8,
                state = opening.position,
                orientation = orientation,
                squareBorder = squareBoard,
                onClick = onClick,
            )
        }
    }
}
