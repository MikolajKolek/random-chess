package pl.edu.uj.tcs.rchess.view.newgame

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import pl.edu.uj.tcs.rchess.model.ClockSettings
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.util.runIf
import pl.edu.uj.tcs.rchess.view.adapters.DataStateAdapter
import pl.edu.uj.tcs.rchess.view.adapters.DismissibleErrorsAdapter
import pl.edu.uj.tcs.rchess.view.shared.Loading
import pl.edu.uj.tcs.rchess.view.shared.ScrollableColumn
import pl.edu.uj.tcs.rchess.viewmodel.AppContext
import pl.edu.uj.tcs.rchess.viewmodel.NewGameViewModel
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.icon_start_game
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
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
            size = DpSize(700.dp, 400.dp),
        ),
        resizable = false,
    ) {
        DismissibleErrorsAdapter(
            viewModel.errors,
        ) {
            DataStateAdapter(
                viewModel.opponentList,
                "Loading opponent list...",
                "Failed to load opponent list",
            ) { opponentList, _ ->
                if (viewModel.isLoading) {
                    Loading(text = "The bot is getting ready")
                    return@DataStateAdapter
                }

                Row(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Opponent",
                            modifier = Modifier.padding(bottom = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                        )

                        ScrollableColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            leftPadding = true,
                        ) {
                            opponentList.forEach { opponent ->
                                OutlinedCard(
                                    modifier = Modifier.fillMaxWidth().weight(1f),
                                    onClick = { viewModel.selectedOpponent = opponent },
                                    colors = CardDefaults.outlinedCardColors()
                                        .runIf(viewModel.selectedOpponent == opponent) {
                                            copy(
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                                            )
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                    ) {
                                        Text(opponent.name)
                                        opponent.description?.let { Text(it) }
                                        Text("ELO: ${opponent.elo}")
                                    }
                                }
                            }
                        }
                    }

                    Column(Modifier.width(300.dp)) {
                        Text(
                            "Time controls",
                            modifier = Modifier.padding(bottom = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                        )

                        // List taken from Lichess.org
                        // A custom duration picker is currently hard to implement:
                        // - There is no number input in Material3 Compose,
                        // - The slider component is only available for Android,
                        // - Text Fields in our version of Material3 Compose have a very cumbersome API,
                        //   when Jetpack Compose Multiplatform ships with updated Material3,
                        //   the implementation will become much more elegant
                        val clockChoices = linkedMapOf(
                            ClockSettings(1.minutes, 0.seconds) to "1+0 Bullet",
                            ClockSettings(2.minutes, 1.seconds) to "2+1 Bullet",
                            ClockSettings(3.minutes, 0.seconds) to "3+0 Blitz",
                            ClockSettings(3.minutes, 2.seconds) to "3+2 Blitz",
                            ClockSettings(5.minutes, 0.seconds) to "5+0 Blitz",
                            ClockSettings(5.minutes, 3.seconds) to "5+3 Blitz",
                            ClockSettings(10.minutes, 0.seconds) to "10+0 Rapid",
                            ClockSettings(10.minutes, 5.seconds) to "10+5 Rapid",
                            ClockSettings(15.minutes, 10.seconds) to "15+10 Rapid",
                            ClockSettings(30.minutes, 0.seconds) to "30+0 Classical",
                            ClockSettings(30.minutes, 20.seconds) to "30+20 Classical",
                        )
                        var expanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            modifier = Modifier.fillMaxWidth(),
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                        ) {
                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                value = clockChoices.getOrDefault(
                                    viewModel.clockSettings,
                                    "Custom"
                                ),
                                onValueChange = {},
                                readOnly = true,
                                singleLine = true,
                                label = { Text("Clock settings") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                clockChoices.forEach { (settings, label) ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                label,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        },
                                        onClick = {
                                            viewModel.clockSettings = settings
                                            expanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    )
                                }
                            }
                        }

                        Text(
                            "Starting color",
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                        )

                        // TODO: Style with Material 3 Expressive,
                        //  when the component library update is available in Compose for Desktop
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            val colorChoices = listOf(
                                PlayerColor.WHITE to "White",
                                null to "Any",
                                PlayerColor.BLACK to "Black",
                            )

                            colorChoices.forEachIndexed { index, (choice, label) ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = colorChoices.size
                                    ),
                                    onClick = { viewModel.startingPlayerColor = choice },
                                    selected = viewModel.startingPlayerColor == choice,
                                    label = { Text(label) }
                                )
                            }
                        }

                        Text(
                            "Include in rankings",
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                        )

                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            val rankedChoices = listOf(
                                true to "Ranked",
                                false to "Unranked",
                            )

                            rankedChoices.forEachIndexed { index, (choice, label) ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = rankedChoices.size
                                    ),
                                    onClick = { viewModel.isRanked = choice },
                                    selected = viewModel.isRanked == choice,
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
                                enabled = viewModel.readyToSubmit,
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
    }
}
