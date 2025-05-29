package pl.edu.uj.tcs.rchess.view.newgame

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import pl.edu.uj.tcs.rchess.view.PlaceholderScreen

@Composable
fun NewGameDialog(onCancel: () -> Unit,) {
    DialogWindow(
        title = "Start new game",
        onCloseRequest = { onCancel() },
        state = rememberDialogState(
            position = WindowPosition(Alignment.Center),
            size = DpSize(750.dp, 500.dp),
        ),
        resizable = false,
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PlaceholderScreen(
                modifier = Modifier
                    .width(0.dp)
                    .weight(1f),
                text = "Bot difficulty picker",
            )

            Column(
                modifier = Modifier.width(400.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Button(
                        onClick = {
                            TODO("Implement submit action")
                        },
                    ) {
                        Text("Start game")
                    }
                }
            }
        }
    }
}
