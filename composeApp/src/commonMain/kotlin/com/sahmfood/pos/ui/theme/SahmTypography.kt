package com.sahmfood.pos.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val sans = FontFamily.SansSerif
private val mono = FontFamily.Monospace

/**
 * Type scale with deliberate letter-spacing tweaks. Negative tracking on
 * display/headline sizes is the single biggest move to shift the feel from
 * "default M3 app" to "brand."
 */
val SahmTypography: Typography = Typography(
    displayLarge = TextStyle(fontFamily = sans, fontSize = 57.sp, lineHeight = 64.sp,
        fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
    displayMedium = TextStyle(fontFamily = sans, fontSize = 45.sp, lineHeight = 52.sp,
        fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
    displaySmall = TextStyle(fontFamily = sans, fontSize = 36.sp, lineHeight = 44.sp,
        fontWeight = FontWeight.Bold, letterSpacing = (-0.25).sp),

    headlineLarge = TextStyle(fontFamily = sans, fontSize = 32.sp, lineHeight = 40.sp,
        fontWeight = FontWeight.SemiBold, letterSpacing = (-0.25).sp),
    headlineMedium = TextStyle(fontFamily = sans, fontSize = 28.sp, lineHeight = 36.sp,
        fontWeight = FontWeight.SemiBold, letterSpacing = (-0.25).sp),
    headlineSmall = TextStyle(fontFamily = sans, fontSize = 24.sp, lineHeight = 32.sp,
        fontWeight = FontWeight.SemiBold, letterSpacing = (-0.25).sp),

    titleLarge = TextStyle(fontFamily = sans, fontSize = 22.sp, lineHeight = 28.sp,
        fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontFamily = sans, fontSize = 16.sp, lineHeight = 24.sp,
        fontWeight = FontWeight.SemiBold, letterSpacing = 0.1.sp),
    titleSmall = TextStyle(fontFamily = sans, fontSize = 14.sp, lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold, letterSpacing = 0.1.sp),

    bodyLarge = TextStyle(fontFamily = sans, fontSize = 16.sp, lineHeight = 26.sp,
        fontWeight = FontWeight.Normal, letterSpacing = 0.3.sp),
    bodyMedium = TextStyle(fontFamily = sans, fontSize = 14.sp, lineHeight = 22.sp,
        fontWeight = FontWeight.Normal, letterSpacing = 0.2.sp),
    bodySmall = TextStyle(fontFamily = sans, fontSize = 12.sp, lineHeight = 16.sp,
        fontWeight = FontWeight.Normal, letterSpacing = 0.3.sp),

    labelLarge = TextStyle(fontFamily = sans, fontSize = 14.sp, lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold, letterSpacing = 0.1.sp),
    labelMedium = TextStyle(fontFamily = sans, fontSize = 12.sp, lineHeight = 16.sp,
        fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp),
    labelSmall = TextStyle(fontFamily = sans, fontSize = 11.sp, lineHeight = 16.sp,
        fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp),
)

val ReceiptMonoStyle: TextStyle = TextStyle(
    fontSize = 13.sp,
    fontFamily = mono,
    lineHeight = 18.sp
)
