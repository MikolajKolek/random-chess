package pl.edu.uj.tcs.rchess.view.shared

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.runBlocking

@Composable
fun ExportField(
    value: String,
    downloadEnabled: Boolean,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedCard(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        ) {
            SelectionContainer {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = value,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            val clipboardManager = LocalClipboardManager.current

            if (downloadEnabled) {
                Button(
                    enabled = false,
                    onClick = {
                        TODO("Implement file download")
                    }
                ) {
                    Text("Download")
                }
            }

            Button(
                onClick = {
                    runBlocking {
                        clipboardManager.setText(AnnotatedString(value))
                    }
                }
            ) {
                Text("Copy to clipboard")
            }
        }
    }
}
