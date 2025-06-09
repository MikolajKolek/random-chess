package pl.edu.uj.tcs.rchess.view.gamesidebar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.icon_pin_enabled_small
import rchess.composeapp.generated.resources.icon_pin_small

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Field(
    label: String,
    pinned: Boolean? = null,
    onPinnedChange: (newValue: Boolean) -> Unit = {},
    content: @Composable () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.padding(bottom = 4.dp).heightIn(min = 32.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
            )

            if (pinned != null) {
                val pinLabel = if (pinned) "Unpin" else "Pin"
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = { PlainTooltip { Text(pinLabel) } },
                    state = rememberTooltipState(),
                ) {
                    IconToggleButton(
                        checked = pinned,
                        modifier = Modifier.size(32.dp),
                        onCheckedChange = onPinnedChange,
                    ) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(
                                if (pinned) Res.drawable.icon_pin_enabled_small
                                else Res.drawable.icon_pin_small
                            ),
                            contentDescription = "pinLabel",
                        )
                    }
                }
            }
        }
        content()
    }
}