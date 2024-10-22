// Theme.kt
//package com.yourappname.ui.theme
package com.husn.fashionapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Define the beige background color
val Beige = Color(0xFFF5F5DC)
val TextColor = Color(0xFF333333)

// Light and dark theme color schemes
private val LightColorScheme = lightColorScheme(
//    primary = Color(0xFF4B5563),
    primary = Color(0xFF212529),
    onPrimary = Color.White,
    secondary = Color(0xFFBDB76B),
    onSecondary = Color.White,
    background = Color.Transparent,
    onBackground = TextColor,
//    surface = Beige,
    surface = Color.Transparent,
    onSurface = TextColor
)

private val DarkColorScheme = darkColorScheme(
//    primary = Color(0xFF4B5563),
    primary = Color(0xFF212529),
    onPrimary = Color.Black,
    secondary = Color(0xFFBDB76B),
    onSecondary = Color.Black,
    background = Color.Transparent,
//    background = Color(0xFF2B2B2B),
    onBackground = Color.White,
//    surface = Color(0xFF2B2B2B),
    surface = Color.Transparent,
    onSurface = Color.White
)

val AppFontFamily = FontFamily.Serif
val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    )
    // Add more text styles as needed
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
        typography = AppTypography,
        content = content
    )
}
