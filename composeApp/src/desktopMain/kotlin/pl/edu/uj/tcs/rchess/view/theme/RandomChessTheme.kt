package pl.edu.uj.tcs.rchess.view.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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

    val colorScheme = MaterialTheme.colorScheme.copy(
        primary                 = Color(0xFF582630),
        onPrimary               = Color.White,
        primaryContainer        = Color(0xFFE0B8BF),
        onPrimaryContainer      = Color(0xFF582630),

        secondary               = Color(0xFFC17181),
        secondaryContainer      = Color(0xFFECD4DA),

        tertiary                = Color(0xFF326771),
        onTertiary              = Color.White,
        tertiaryContainer       = Color(0xFFC6E1E6),
        onTertiaryContainer     = Color(0xFF1F4047),

        background              = Color(0xFFFAF7F9),
        surface                 = Color(0xFFFAF7F9),
        surfaceVariant          = Color(0xFFFAF7F9),

        surfaceContainerLowest  = Color(0xFFEDEBEC),
        surfaceContainerLow     = Color(0xFFEDEBEC),
        surfaceContainer        = Color(0xFFEDEBEC),
        surfaceContainerHigh    = Color(0xFFEDEBEC),
        surfaceContainerHighest = Color(0xFFEDEBEC),
    )

    MaterialTheme(
        typography = typography,
        content = content,
        colorScheme = colorScheme,
    )
}
