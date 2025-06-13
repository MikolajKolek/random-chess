package pl.edu.uj.tcs.rchess.view.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.api.entity.ServiceAccount
import pl.edu.uj.tcs.rchess.view.board.icon
import pl.edu.uj.tcs.rchess.view.shared.format

@Composable
fun AccountListItem(account: ServiceAccount) {
    Row(
        modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.widthIn(min = 216.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            account.service.icon?.let { icon ->
                Image(
                    modifier = Modifier.padding(end = 24.dp).size(28.dp),
                    painter = painterResource(icon),
                    contentDescription = "Service logo",
                )
            }
            Text(
                account.service.format(),
            )
        }

        Text(
            account.displayName,
        )
    }
}
