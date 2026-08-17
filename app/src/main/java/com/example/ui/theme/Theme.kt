package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PowerTrackGreen,
    onPrimary = Color.Black,
    primaryContainer = PowerTrackGreenContainer,
    onPrimaryContainer = PowerTrackGreenLight,
    secondary = LoadBlue,
    onSecondary = Color.Black,
    secondaryContainer = LoadBlueContainer,
    onSecondaryContainer = LoadBlue,
    tertiary = SolarAmber,
    onTertiary = Color.Black,
    tertiaryContainer = SolarAmberContainer,
    onTertiaryContainer = SolarYellow,
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkSurfaceBorder,
    outlineVariant = DarkSurfaceElevated,
    error = StatusError,
    onError = Color.White,
    errorContainer = StatusErrorBg,
    onErrorContainer = StatusError
)

private val LightColorScheme = lightColorScheme(
    primary = PowerTrackGreenDark,
    onPrimary = Color.White,
    primaryContainer = PowerTrackGreenLight.copy(alpha = 0.2f),
    onPrimaryContainer = PowerTrackGreenDark,
    secondary = LoadBlueDark,
    onSecondary = Color.White,
    secondaryContainer = LoadBlue.copy(alpha = 0.2f),
    onSecondaryContainer = LoadBlueDark,
    tertiary = SolarAmber,
    onTertiary = Color.Black,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceCard,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightSurfaceBorder,
    error = StatusError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek solar dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
