package com.codeandart.systemmonitor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00D9FF),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF004E5C),
    onPrimaryContainer = Color(0xFF7FFAFF),
    secondary = Color(0xFF1DB8D8),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF004E5C),
    onSecondaryContainer = Color(0xFF7FFAFF),
    tertiary = Color(0xFFFF6B35),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFF8C61),
    onTertiaryContainer = Color(0xFF000000),
    background = Color(0xFF0A0E27),
    onBackground = Color(0xFFE1E1E6),
    surface = Color(0xFF121621),
    onSurface = Color(0xFFE1E1E6),
    surfaceVariant = Color(0xFF1A1F3A),
    onSurfaceVariant = Color(0xFFC4C7D0),
    error = Color(0xFFFF6B6B),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF8B0000),
    onErrorContainer = Color(0xFFFFB4B4)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0073A8),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB3E5FC),
    onPrimaryContainer = Color(0xFF001F3F),
    secondary = Color(0xFF0097A7),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFB2EBF2),
    onSecondaryContainer = Color(0xFF002830),
    tertiary = Color(0xFFFF6B35),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD9CC),
    onTertiaryContainer = Color(0xFF330000),
    background = Color(0xFFFAFBFE),
    onBackground = Color(0xFF1A1B1F),
    surface = Color(0xFFFAFBFE),
    onSurface = Color(0xFF1A1B1F),
    surfaceVariant = Color(0xFFE0E2EC),
    onSurfaceVariant = Color(0xFF49454F),
    error = Color(0xFFFF6B6B),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF370B0E)
)

@Composable
fun SystemMonitorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}