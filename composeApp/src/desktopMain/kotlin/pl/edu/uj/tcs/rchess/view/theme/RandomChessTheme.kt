package pl.edu.uj.tcs.rchess.view.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.SpaceGrotesk

@Composable
fun RandomChessTheme(
    content: @Composable () -> Unit,
) {
    val spaceGroteskFamily = FontFamily(
        Font(Res.font.SpaceGrotesk)
    )

    val typography = MaterialTheme.typography.run {
        fun transform(style: TextStyle) = style.copy(
            fontFamily = spaceGroteskFamily,
            fontFeatureSettings = "tnum",
            fontWeight = FontWeight.Medium,
        )

        Typography(
            displayLarge    = transform(displayLarge),
            displayMedium   = transform(displayMedium),
            displaySmall    = transform(displaySmall),
            headlineLarge   = transform(headlineLarge),
            headlineMedium  = transform(headlineMedium),
            headlineSmall   = transform(headlineSmall),
            titleLarge      = transform(titleLarge),
            titleMedium     = transform(titleMedium),
            titleSmall      = transform(titleSmall),
            bodySmall       = transform(bodySmall),
            bodyMedium      = transform(bodyMedium),
            bodyLarge       = transform(bodyLarge),
            labelLarge      = transform(labelLarge),
            labelMedium     = transform(labelMedium),
            labelSmall      = transform(labelSmall),
        )
    }

    MaterialTheme(
        typography = typography,
        content = content,
    )
}
