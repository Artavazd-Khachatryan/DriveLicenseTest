package com.drive.license.test.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import drivelicensetest.ui.generated.resources.Inter
import drivelicensetest.ui.generated.resources.Res
import org.jetbrains.compose.resources.Font

/**
 * Inter is a single variable font; each weight is pinned through the `wght` axis so the
 * renderer uses the real master instead of synthesizing bold, which keeps text crisp on
 * both Android and iOS.
 */
@Composable
private fun interFamily(): FontFamily {
    fun weight(value: Int) = FontVariation.Settings(FontVariation.weight(value))
    return FontFamily(
        Font(Res.font.Inter, FontWeight.Normal, variationSettings = weight(400)),
        Font(Res.font.Inter, FontWeight.Medium, variationSettings = weight(500)),
        Font(Res.font.Inter, FontWeight.SemiBold, variationSettings = weight(600)),
        Font(Res.font.Inter, FontWeight.Bold, variationSettings = weight(700)),
    )
}

@Composable
fun AppTypography(): Typography {
    val inter = interFamily()
    return Typography(
        displayLarge = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Bold,
            fontSize = 48.sp,
            lineHeight = 56.sp,
            letterSpacing = (-1).sp,
        ),
        displayMedium = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = (-0.5).sp,
        ),
        displaySmall = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            lineHeight = 38.sp,
            letterSpacing = (-0.25).sp,
        ),
        headlineLarge = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            lineHeight = 32.sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 28.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 26.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.1.sp,
        ),
        titleSmall = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.2.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.3.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp,
        ),
    )
}
