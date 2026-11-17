package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val QuantumDarkColorScheme = darkColorScheme(
    primary = NeonRed,
    onPrimary = Color.White,
    primaryContainer = GlassCardBgElevated,
    onPrimaryContainer = TextPrimary,
    secondary = SignalGreen,
    onSecondary = Color.Black,
    secondaryContainer = SignalGreenBg,
    onSecondaryContainer = SignalGreen,
    tertiary = BinanceYellow,
    background = CrimsonBgDeep,
    onBackground = TextPrimary,
    surface = GlassCardBg,
    onSurface = TextPrimary,
    surfaceVariant = CrimsonBgLight,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorderSubtle,
    error = NeonRedBright,
    onError = Color.White
)

@Composable
fun TSQuantumTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = QuantumDarkColorScheme,
        typography = QuantumTypography,
        content = content
    )
}
