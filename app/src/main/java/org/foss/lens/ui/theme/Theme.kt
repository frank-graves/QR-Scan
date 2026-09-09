// org/foss/lens/ui/theme/Theme.kt
package org.foss.lens.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Zinc + un solo acento verde: sobrio, sin ruido, con intención.
private val LightColors = lightColorScheme(
    primary = Color(0xFF0E7A4B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB7F2D0),
    onPrimaryContainer = Color(0xFF00210F),
    secondary = Color(0xFF4B6657),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFFAFCF9),
    onBackground = Color(0xFF171D1A),
    surface = Color(0xFFF2F5F1),
    onSurface = Color(0xFF171D1A),
    surfaceVariant = Color(0xFFDCE6DE),
    onSurfaceVariant = Color(0xFF404943),
    outline = Color(0xFF707973),
    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF6EE7A5),
    onPrimary = Color(0xFF00391E),
    primaryContainer = Color(0xFF00522F),
    onPrimaryContainer = Color(0xFFB7F2D0),
    secondary = Color(0xFFB5CCBB),
    onSecondary = Color(0xFF21362A),
    background = Color(0xFF0B0F14),
    onBackground = Color(0xFFE0E4E2),
    surface = Color(0xFF12171B),
    onSurface = Color(0xFFE0E4E2),
    surfaceVariant = Color(0xFF1D2622),
    onSurfaceVariant = Color(0xFFC0CBC3),
    outline = Color(0xFF8A958E),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun LensTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
