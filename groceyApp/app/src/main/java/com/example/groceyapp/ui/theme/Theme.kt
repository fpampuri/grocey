package com.example.groceyapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BrandGreenDarkTheme,
    onPrimary = Color.Black,
    primaryContainer = BrandGreenDeepDarkTheme,
    onPrimaryContainer = Color.Black,
    secondary = AccentGoldDark,
    onSecondary = Color.Black,
    secondaryContainer = AccentGoldHoverDark,
    onSecondaryContainer = Color.Black,
    tertiary = BrandGreenLightDarkTheme,
    onTertiary = Color.Black,
    background = NeutralBackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = NeutralContainerDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = TextTertiaryDark,
    error = DeleteRedDark,
    onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = BrandGreen,
    onPrimary = Color.White,
    primaryContainer = BrandGreenDark,
    onPrimaryContainer = Color.White,
    secondary = AccentGold,
    onSecondary = Color.Black,
    secondaryContainer = AccentGoldHover,
    onSecondaryContainer = Color.Black,
    tertiary = BrandGreenLight,
    onTertiary = Color.White,
    background = NeutralBackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = NeutralBackgroundLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = TextTertiaryLight,
    error = DeleteRedLight,
    onError = Color.White
)

@Composable
fun GroceyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep dynamic colors opt-in to preserve the shared palette across platforms.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
