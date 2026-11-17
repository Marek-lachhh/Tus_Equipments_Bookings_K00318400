package com.example.tusequipmentbooking.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val lightColors = lightColorScheme(
    primary = TusGold,
    onPrimary = Color.White,
    primaryContainer = TusGoldLight,
    onPrimaryContainer = TusText,
    secondary = TusBrown,
    onSecondary = Color.White,
    secondaryContainer = TusGoldLight,
    onSecondaryContainer = TusText,
    tertiary = TusBrown,
    background = TusCream,
    onBackground = TusText,
    surface = TusCream,
    onSurface = TusText,
    surfaceVariant = TusCreamDark,
    onSurfaceVariant = TusTextSoft,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = TusCream,
    surfaceContainer = TusCreamDark,
    surfaceContainerHigh = TusCreamDark,
    surfaceContainerHighest = TusCreamDeeper,
    outline = TusGold,
    outlineVariant = TusGoldLight
)

private val darkColors = darkColorScheme(
    primary = DarkGold,
    onPrimary = DarkOnGold,
    primaryContainer = DarkGoldContainer,
    onPrimaryContainer = DarkCream,
    secondary = DarkCreamSoft,
    onSecondary = DarkOnGold,
    secondaryContainer = DarkIndicator,
    onSecondaryContainer = DarkCream,
    tertiary = DarkGold,
    background = DarkBackground,
    onBackground = DarkCream,
    surface = DarkBackground,
    onSurface = DarkCream,
    surfaceVariant = DarkCard,
    onSurfaceVariant = DarkCreamSoft,
    surfaceContainerLowest = Color(0xFF14110C),
    surfaceContainerLow = DarkDrawer,
    surfaceContainer = DarkBar,
    surfaceContainerHigh = Color(0xFF2E271D),
    surfaceContainerHighest = DarkCard,
    outline = DarkOutline,
    outlineVariant = DarkIndicator
)

@Composable
fun TusTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColors else lightColors,
        content = content
    )
}