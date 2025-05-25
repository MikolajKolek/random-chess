package pl.edu.uj.tcs.rchess.view.gamesidebar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.model.Fen.Companion.toFenString
import pl.edu.uj.tcs.rchess.model.state.BoardState
import pl.edu.uj.tcs.rchess.view.shared.ExportField

@Composable
fun ExportTab(
    modifier: Modifier = Modifier,
    currentBoardState: BoardState,
) {
    Column(
        modifier = Modifier.padding(8.dp).then(modifier),
    ) {
        ExportField(
            label = "FEN after current move",
            value = currentBoardState.toFenString(),
        )

        ExportField(
            label = "PGN",
            value = "TODO", // TODO
        )
    }
}
