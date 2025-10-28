package com.example.tiendastore.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimary,
    onPrimary = OnPrimary,
    secondary = PurpleSecondary,
    onSecondary = OnSecondary,
    tertiary = BlueTertiary,
    background = DarkBackground,
    onBackground = OnDark,
    surface = DarkSurface,
    onSurface = OnDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = OnDark.copy(alpha = 0.85f),
    outline = Outline,
    error = ErrorRed,
    onError = OnError
)

// Light de cortesía (no principal). Mantiene identidad.
private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = OnPrimary,
    secondary = PurpleSecondary,
    onSecondary = OnSecondary,
    tertiary = BlueTertiary,
    background = Color(0xFFF6F7FF),
    onBackground = Color(0xFF0E0F1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0E0F1A),
    surfaceVariant = Color(0xFFE7E8FF),
    onSurfaceVariant = Color(0xFF272B55),
    outline = Color(0xFFB6BAE6),
    error = ErrorRed,
    onError = OnError
)

@Composable
fun TiendaStoreTheme(
    darkTheme: Boolean = true, // por defecto oscuro como definimos
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme || isSystemInDarkTheme()) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
