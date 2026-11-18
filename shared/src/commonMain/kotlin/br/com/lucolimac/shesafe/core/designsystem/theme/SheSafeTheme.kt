package br.com.lucolimac.shesafe.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import shesafe.shared.generated.resources.Res
import shesafe.shared.generated.resources.roboto_bold
import shesafe.shared.generated.resources.roboto_light
import shesafe.shared.generated.resources.roboto_medium
import shesafe.shared.generated.resources.roboto_regular

/** Brand primary pale violet-red color (`#D87093`). */
val PaleVioletRed = Color(0xFFD87093)

/** Brand accent rose color (`#C8758F`) used for destructive or confirmation actions. */
val RoseAction = Color(0xFFC8758F)

/** Light theme off-white background color (`#F2F2F2`). */
val WhiteSmoke = Color(0xFFF2F2F2)

/** Brand secondary muted teal color (`#73B5B4`). */
val GreenSheen = Color(0xFF73B5B4)

/** Material 3 light color scheme configured for SheSafe. */
private val LightColorScheme = lightColorScheme(
    primary = PaleVioletRed,
    secondary = GreenSheen,
    background = WhiteSmoke,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    primaryContainer = PaleVioletRed,
)

/** Material 3 dark color scheme configured for SheSafe. */
private val DarkColorScheme = darkColorScheme(
    primary = PaleVioletRed,
    secondary = GreenSheen,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    primaryContainer = PaleVioletRed,
)

/** Rounded corner shape scale applied across SheSafe Material 3 components. */
val SheSafeShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp),
)

/**
 * Loads the bundled multiplatform Roboto [FontFamily] across light, regular, medium, and bold weights.
 *
 * @return Configured Roboto [FontFamily] backed by Compose Multiplatform font resources.
 */
@Composable
private fun rememberRobotoFontFamily(): FontFamily = FontFamily(
    Font(Res.font.roboto_light, FontWeight.Light),
    Font(Res.font.roboto_regular, FontWeight.Normal),
    Font(Res.font.roboto_medium, FontWeight.Medium),
    Font(Res.font.roboto_bold, FontWeight.Bold),
)

/**
 * Builds the Material 3 [Typography] scale for SheSafe using [rememberRobotoFontFamily].
 *
 * @return Complete [Typography] definition spanning display, headline, title, body, and label styles.
 */
@Composable
private fun rememberSheSafeTypography(): Typography {
    val roboto = rememberRobotoFontFamily()
    return Typography(
        displayLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 56.sp, fontFamily = roboto),
        displayMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 48.sp, fontFamily = roboto),
        displaySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 36.sp, fontFamily = roboto),
        headlineLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 32.sp, fontFamily = roboto),
        headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 28.sp, fontFamily = roboto),
        headlineSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp, fontFamily = roboto),
        titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, fontFamily = roboto),
        titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, fontFamily = roboto),
        titleSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, fontFamily = roboto),
        bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, fontFamily = roboto),
        bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, fontFamily = roboto),
        bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, fontFamily = roboto),
        labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, fontFamily = roboto),
        labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, fontFamily = roboto),
        labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, fontFamily = roboto),
    )
}

/**
 * Root Material 3 theme wrapper applying SheSafe color schemes, Roboto typography, and component shapes.
 *
 * @param darkTheme Whether dark color tokens should be used; defaults to [isSystemInDarkTheme].
 * @param content Composable hierarchy rendered within the themed [MaterialTheme] context.
 */
@Composable
fun SheSafeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = rememberSheSafeTypography(),
        shapes = SheSafeShapes,
        content = content,
    )
}
