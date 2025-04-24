package br.com.lucolimac.shesafe.android.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PaleVioletRed,
    secondary = GreenSheen,
    background = WhiteSmoke,
    surface = Color.White,  // Card backgrounds
    onPrimary = Color.White,  // Text on primary color (pink)
    onBackground = Color.Black, // Text on light gray background
    onSurface = Color.Black,   // Text on white surface
    primaryContainer = PaleVioletRed, // Container background for primary color

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)
private val DarkColorScheme = LightColorScheme

@Composable
fun SheSafeTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) LightColorScheme else DarkColorScheme,
        typography = SheSafeTypography,
        shapes = SheSafeShapes,
        content = content
    )
}