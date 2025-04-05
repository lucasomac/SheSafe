package br.com.lucolimac.shesafe.android.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.com.lucolimac.shesafe.R

// Define Roboto Font Family
internal val Roboto = FontFamily(
    Font(R.font.roboto_light, FontWeight.Light),
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_bold, FontWeight.Bold)
)
internal val SheSafeTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Normal, fontSize = 57.sp, fontFamily = Roboto,
    ), displayMedium = TextStyle(
        fontWeight = FontWeight.Normal, fontSize = 45.sp, fontFamily = Roboto,
    ), displaySmall = TextStyle(
        fontWeight = FontWeight.Normal, fontSize = 36.sp, fontFamily = Roboto,
    ), headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold, fontSize = 32.sp, fontFamily = Roboto,
    ), headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold, fontSize = 28.sp, fontFamily = Roboto,
    ), headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold, fontSize = 24.sp, fontFamily = Roboto,
    ), titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold, fontSize = 22.sp, fontFamily = Roboto,
    ), titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold, fontSize = 16.sp, fontFamily = Roboto,
    ), titleSmall = TextStyle(
        fontWeight = FontWeight.SemiBold, fontSize = 14.sp, fontFamily = Roboto,
    ), bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal, fontSize = 16.sp, fontFamily = Roboto,
    ), bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal, fontSize = 14.sp, fontFamily = Roboto,
    ), bodySmall = TextStyle(
        fontWeight = FontWeight.Normal, fontSize = 12.sp, fontFamily = Roboto,
    ), labelLarge = TextStyle(
        fontWeight = FontWeight.Medium, fontSize = 14.sp, fontFamily = Roboto,
    ), labelMedium = TextStyle(
        fontWeight = FontWeight.Medium, fontSize = 12.sp, fontFamily = Roboto,
    ), labelSmall = TextStyle(
        fontWeight = FontWeight.Medium, fontSize = 11.sp, fontFamily = Roboto,
    )
)
