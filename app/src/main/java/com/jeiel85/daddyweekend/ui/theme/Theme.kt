package com.jeiel85.daddyweekend.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CozyDarkPrimary,
    onPrimary = CozyDarkBg,
    secondary = CozyDarkSecondary,
    onSecondary = CozyDarkBg,
    tertiary = CozyDarkTertiary,
    onTertiary = CozyDarkText,
    background = CozyDarkBg,
    onBackground = CozyDarkText,
    surface = CozyDarkSurface,
    onSurface = CozyDarkText,
    surfaceVariant = Color(0xFF2E3B31),
    onSurfaceVariant = CozyDarkSubText,
    error = Color(0xFFE57373),
    onError = Color(0xFF331414)
)

private val LightColorScheme = lightColorScheme(
    primary = DarkGreen,
    onPrimary = Color.White,
    secondary = FieldGreen,
    onSecondary = Color.White,
    tertiary = EarthBrown,
    onTertiary = Color.White,
    background = WarmBg,
    onBackground = DeepCharcoal,
    surface = Color.White,
    onSurface = DeepCharcoal,
    surfaceVariant = WarmSurface,
    onSurfaceVariant = DeepCharcoal,
    error = CoralAccent,
    onError = Color.White,
    outline = SageSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep our custom cozy color branding consistent by setting dynamicColor to false
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
