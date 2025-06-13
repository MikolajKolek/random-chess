package pl.edu.uj.tcs.rchess.view.adapters

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.uj.tcs.rchess.view.shared.ErrorCard
import pl.edu.uj.tcs.rchess.viewmodel.datastate.DismissibleErrorsState

/**
 * A Composable that takes a [DismissibleErrorsState] and displays an [ErrorCard] if there is an error to be dismissed.
 * If there is no error, it calls the [content] lambda.
 */
@Composable
fun DismissibleErrorsAdapter(
    state: DismissibleErrorsState,
    content: @Composable () -> Unit,
) {
    state.topError?.let {
        Box(Modifier.padding(16.dp).fillMaxSize()) {
            ErrorCard(
                modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                headerText = it.header,
                error = it.exception,
                dismissText = "Dismiss",
                onDismiss = { state.dismissTopError() },
                prominent = true,
            )
        }
        return
    }
    content()
}
