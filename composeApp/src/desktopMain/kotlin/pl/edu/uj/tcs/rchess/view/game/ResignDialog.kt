package pl.edu.uj.tcs.rchess.view.game

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.painterResource
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.icon_resign

@Composable
fun ResignDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(Res.drawable.icon_resign),
                contentDescription = "Resign icon",
            )
        },
        title = {
            Text(text = "Resign")
        },
        text = {
            Text(text = "Do you really want to resign and end the game now?")
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Resign")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Keep playing")
            }
        }
    )
}