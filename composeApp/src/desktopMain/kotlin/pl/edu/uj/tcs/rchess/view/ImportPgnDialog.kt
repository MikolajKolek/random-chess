package pl.edu.uj.tcs.rchess.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import pl.edu.uj.tcs.rchess.AppContext
import pl.edu.uj.tcs.rchess.viewmodel.ImportPgnViewModel

@Composable
fun ImportPgnDialog(
    context: AppContext,
    onCancel: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: ImportPgnViewModel = viewModel { ImportPgnViewModel(context) },
) {
    val coroutineScope = rememberCoroutineScope()

    fun submit() {
        coroutineScope.launch {
            viewModel.submit()
            onSuccess()
        }
    }

    DialogWindow(
        title = "Import PGN",
        onCloseRequest = { onCancel() },
        state = rememberDialogState(position = WindowPosition(Alignment.Center)),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextField(
                value = viewModel.pgnInput.value,
                onValueChange = { viewModel.pgnInput.value = it },
                label = { Text("PGN") },
                modifier = Modifier.fillMaxWidth().weight(1f),
                singleLine = false,
                minLines = 3,
            )

            Row {
                Button(
                    onClick = ::submit,
                ) {
                    Text("Import")
                }
            }
        }
    }
}
