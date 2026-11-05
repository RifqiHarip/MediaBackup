package com.rifqi.mediabackup.ui.theme

import android.app.Activity
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BlackRedColorScheme = darkColorScheme(
    primary = Color(0xFFE50914),          // Vibrant Red
    onPrimary = Color.White,
    primaryContainer = Color(0xFF8B0000), // Dark Red/Crimson
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFFF4D4D),        // Accent Coral Red
    onSecondary = Color.Black,
    background = Color(0xFF121212),       // Darkish Black
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),          // Dark Grey Card Surface
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF444444)
)

@Composable
fun BlackRedTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BlackRedColorScheme,
        content = content
    )
}