package pl.edu.uj.tcs.rchess.view.newgame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.view.PlaceholderScreen
import pl.edu.uj.tcs.rchess.view.shared.Loading
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
    val coroutineScope = rememberCoroutineScope()

    fun submit() {
        coroutineScope.launch {
            viewModel.submitAnd {
                onClose()
            }
        }
    }

    DialogWindow(
        title = "Start new game",
        onCloseRequest = { onClose() },
        state = rememberDialogState(
            position = WindowPosition(Alignment.Center),
            size = DpSize(750.dp, 500.dp),
        ),
        resizable = false,
    ) {
        if (viewModel.isLoading) {
            Loading(text = "The bot is getting ready")
            return@DialogWindow
        }

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

                // TODO: Style with Material 3 Expressive
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
                        onClick = ::submit,
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
