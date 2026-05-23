package com.sahmfood.pos.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val sansFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
private val monoFamily = androidx.compose.ui.text.font.FontFamily.Monospace

val SahmTypography: Typography = Typography(
    displaySmall   = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold,  lineHeight = 44.sp, fontFamily = sansFamily),
    headlineLarge  = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.SemiBold, lineHeight = 40.sp, fontFamily = sansFamily),
    headlineMedium = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.SemiBold, lineHeight = 36.sp, fontFamily = sansFamily),
    headlineSmall  = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold, lineHeight = 32.sp, fontFamily = sansFamily),
    titleLarge     = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold, lineHeight = 28.sp, fontFamily = sansFamily),
    titleMedium    = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, lineHeight = 24.sp, fontFamily = sansFamily),
    titleSmall     = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, lineHeight = 20.sp, fontFamily = sansFamily),
    bodyLarge      = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal,   lineHeight = 24.sp, fontFamily = sansFamily),
    bodyMedium     = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal,   lineHeight = 20.sp, fontFamily = sansFamily),
    bodySmall      = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal,   lineHeight = 16.sp, fontFamily = sansFamily),
    labelLarge     = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, lineHeight = 20.sp, fontFamily = sansFamily),
    labelMedium    = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, lineHeight = 16.sp, fontFamily = sansFamily),
    labelSmall     = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, lineHeight = 16.sp, fontFamily = sansFamily),
)

val ReceiptMonoStyle: TextStyle = TextStyle(
    fontSize = 13.sp,
    fontFamily = monoFamily,
    lineHeight = 18.sp
)
