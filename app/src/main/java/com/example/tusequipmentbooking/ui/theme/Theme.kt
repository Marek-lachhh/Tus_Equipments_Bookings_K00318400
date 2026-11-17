package com.example.tusequipmentbooking.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val tusColors = lightColorScheme(
    primary = TusGold,
    onPrimary = Color.White,
    primaryContainer = TusGoldLight,
    onPrimaryContainer = TusText,
    secondary = TusBrown,
    background = TusCream,
    onBackground = TusText,
    surface = TusCream,
    onSurface = TusText,
    surfaceVariant = TusCreamDark,
    onSurfaceVariant = TusText,
    surfaceContainerHighest = TusCreamDark,
    outline = TusGold
)

@Composable
fun TusTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = tusColors, content = content)
}
