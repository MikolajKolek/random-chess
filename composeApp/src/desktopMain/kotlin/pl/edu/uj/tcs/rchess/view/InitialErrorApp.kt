package pl.edu.uj.tcs.rchess.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.view.theme.RandomChessTheme
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.icon_error

fun startInitialErrorApp(error: Exception) = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            size = DpSize(700.dp, 250.dp),
        ),
        title = "Random Chess - loading error",
        resizable = false,
    ) {
        RandomChessTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier.padding(end = 16.dp),
                        painter = painterResource(Res.drawable.icon_error),
                        contentDescription = "Error icon",
                    )

                    Column(
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Error loading the server",
                            style = MaterialTheme.typography.headlineSmall,
                        )

                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = error.message ?: "Unknown error",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}
