// Theme.kt
//package com.yourappname.ui.theme
package com.husn.fashionapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define the beige background color
val Beige = Color(0xFFF5F5DC)
val TextColor = Color(0xFF333333)

// Light and dark theme color schemes
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF835905),
    onPrimary = Color.White,
    secondary = Color(0xFFBDB76B),
    onSecondary = Color.White,
    background = Beige,
    onBackground = TextColor,
    surface = Beige,
    onSurface = TextColor
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8C7851),
    onPrimary = Color.Black,
    secondary = Color(0xFFBDB76B),
    onSecondary = Color.Black,
    background = Color(0xFF2B2B2B),
    onBackground = Color.White,
    surface = Color(0xFF2B2B2B),
    onSurface = Color.White
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
