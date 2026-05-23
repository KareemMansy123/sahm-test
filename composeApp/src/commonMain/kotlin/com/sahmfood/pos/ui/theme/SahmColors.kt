package com.sahmfood.pos.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Sahm Food brand palette. Warm saffron-amber primary, deep charcoal
 * surfaces, vivid teal secondary, green tertiary for success states.
 * Values are from the design spec.
 */
val SahmLightColors: ColorScheme = lightColorScheme(
    primary            = Color(0xFFD4820A),
    onPrimary          = Color(0xFFFFFFFF),
    primaryContainer   = Color(0xFFFFE0B2),
    onPrimaryContainer = Color(0xFF4A2800),
    secondary          = Color(0xFF1A6B7C),
    onSecondary        = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFB2EBF2),
    onSecondaryContainer = Color(0xFF002A33),
    tertiary           = Color(0xFF2E7D32),
    onTertiary         = Color(0xFFFFFFFF),
    tertiaryContainer  = Color(0xFFC8E6C9),
    onTertiaryContainer = Color(0xFF00210B),
    error              = Color(0xFFB3261E),
    onError            = Color(0xFFFFFFFF),
    errorContainer     = Color(0xFFF9DEDC),
    onErrorContainer   = Color(0xFF410E0B),
    background         = Color(0xFFFFFBF5),
    onBackground       = Color(0xFF1C1B1A),
    surface            = Color(0xFFFFFFFF),
    onSurface          = Color(0xFF1C1B1A),
    surfaceVariant     = Color(0xFFF3E6D4),
    onSurfaceVariant   = Color(0xFF4E4239),
    outline            = Color(0xFF7F6E62),
    outlineVariant     = Color(0xFFD4C4B8),
    scrim              = Color(0xFF000000),
    inverseSurface     = Color(0xFF312F2E),
    inverseOnSurface   = Color(0xFFF4F0EF),
    inversePrimary     = Color(0xFFFFB951),
    surfaceTint        = Color(0xFFD4820A),
)

val SahmDarkColors: ColorScheme = darkColorScheme(
    primary            = Color(0xFFFFB951),
    onPrimary          = Color(0xFF4A2800),
    primaryContainer   = Color(0xFF6B3E00),
    onPrimaryContainer = Color(0xFFFFE0B2),
    secondary          = Color(0xFF80CBC4),
    onSecondary        = Color(0xFF003740),
    secondaryContainer = Color(0xFF004E5B),
    onSecondaryContainer = Color(0xFFB2EBF2),
    tertiary           = Color(0xFFA5D6A7),
    onTertiary         = Color(0xFF003910),
    tertiaryContainer  = Color(0xFF1B4D1F),
    onTertiaryContainer = Color(0xFFC8E6C9),
    error              = Color(0xFFF2B8B5),
    onError            = Color(0xFF601410),
    background         = Color(0xFF141210),
    onBackground       = Color(0xFFE7E1DA),
    surface            = Color(0xFF1E1B18),
    onSurface          = Color(0xFFE7E1DA),
    surfaceVariant     = Color(0xFF2E2924),
    onSurfaceVariant   = Color(0xFFD4C4B8),
    outline            = Color(0xFF9E8E82),
    outlineVariant     = Color(0xFF4E4239),
    inverseSurface     = Color(0xFFE7E1DA),
    inverseOnSurface   = Color(0xFF312F2E),
    inversePrimary     = Color(0xFFD4820A),
    surfaceTint        = Color(0xFFFFB951),
)
