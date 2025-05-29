package pl.edu.uj.tcs.rchess.view.newgame

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.view.PlaceholderScreen
import pl.edu.uj.tcs.rchess.viewmodel.AppContext
import pl.edu.uj.tcs.rchess.viewmodel.NewGameViewModel
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.icon_start_game

@Composable
fun NewGameDialog(
    context: AppContext,
    onClose: () -> Unit,
    viewModel: NewGameViewModel = viewModel { NewGameViewModel(context) }
) {
    DialogWindow(
        title = "Start new game",
        onCloseRequest = { onClose() },
        state = rememberDialogState(
            position = WindowPosition(Alignment.Center),
            size = DpSize(750.dp, 500.dp),
        ),
        resizable = false,
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PlaceholderScreen(
                modifier = Modifier
                    .width(0.dp)
                    .weight(1f),
                text = "Bot difficulty picker",
            )

            Column(
                modifier = Modifier.width(300.dp),
            ) {
                Text(
                    "Starting color",
                    style = MaterialTheme.typography.labelLarge,
                )

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    val choices = listOf(
                        PlayerColor.WHITE to "White",
                        null to "Any",
                        PlayerColor.BLACK to "Black",
                    )

                    choices.forEachIndexed { index, (choice, label) ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = choices.size),
                            onClick = { viewModel.startingPlayerColor = choice },
                            selected = viewModel.startingPlayerColor == choice,
                            label = { Text(label) }
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Button(
                        onClick = {
                            TODO("Implement submit action")
                        },
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_start_game),
                            contentDescription = "New game",
                            modifier = Modifier.padding(end = 8.dp),
                        )

                        Text("Start game")
                    }
                }
            }
        }
    }
}
