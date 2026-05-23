package com.sahmfood.pos.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Sahm Food brand palette — premium spec. Warm-shifted neutrals (not blue-grey),
 * deep saffron brand, teal accent for success / non-brand CTAs.
 */

// Brand
val BrandPrimary = Color(0xFFD4820A)
val BrandPrimaryDark = Color(0xFFA85E00)
val BrandPrimaryLight = Color(0xFFF5A832)
val BrandOnPrimary = Color(0xFFFFFFFF)
val BrandPrimaryContainer = Color(0xFFFDECC8)
val BrandOnPrimaryContainer = Color(0xFF3D2000)

// Accent — deep teal for success / secondary CTAs
val AccentTeal = Color(0xFF007B6E)
val AccentTealLight = Color(0xFF00A896)
val AccentOnTeal = Color(0xFFFFFFFF)
val AccentContainer = Color(0xFFC8F0EB)

// Warm neutrals (overrides M3 grey-blue defaults)
val Neutral0 = Color(0xFFFFFFFF)
val Neutral5 = Color(0xFFFBF8F4)
val Neutral10 = Color(0xFFF5EFE6)
val Neutral20 = Color(0xFFEDE3D5)
val Neutral40 = Color(0xFFC4B49A)
val Neutral60 = Color(0xFF8E7D66)
val Neutral80 = Color(0xFF4A3F32)
val Neutral95 = Color(0xFF1C1610)

// Elevation tints (warm)
val Elevation0 = Color(0xFFF5EFE6)
val Elevation1 = Color(0xFFFBF4EA)
val Elevation2 = Color(0xFFFDF7EE)
val Elevation3 = Color(0xFFFFFFFF)

// Semantic
val SahmError = Color(0xFFC0392B)
val SahmErrorContainer = Color(0xFFFDEDEA)
val SahmWarning = Color(0xFFF39C12)
val SahmInfo = Color(0xFF2980B9)

// Category placeholder gradients
val CatBurgers = listOf(Color(0xFFFF6B35), Color(0xFFD4520A))
val CatPizza = listOf(Color(0xFFC0392B), Color(0xFF8B0000))
val CatDrinks = listOf(Color(0xFF2980B9), Color(0xFF1A5276))
val CatDesserts = listOf(Color(0xFF8E44AD), Color(0xFF5B2C6F))
val CatSides = listOf(Color(0xFF27AE60), Color(0xFF1A7A45))
val CatDefault = listOf(BrandPrimaryLight, BrandPrimaryDark)

fun categoryGradient(category: String): List<Color> = when (category.lowercase()) {
    "burgers" -> CatBurgers
    "pizza" -> CatPizza
    "drinks" -> CatDrinks
    "desserts" -> CatDesserts
    "sides" -> CatSides
    else -> CatDefault
}

val SahmLightColors: ColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = BrandOnPrimary,
    primaryContainer = BrandPrimaryContainer,
    onPrimaryContainer = BrandOnPrimaryContainer,
    secondary = AccentTeal,
    onSecondary = AccentOnTeal,
    secondaryContainer = AccentContainer,
    onSecondaryContainer = Color(0xFF002A26),
    tertiary = AccentTeal,
    onTertiary = AccentOnTeal,
    tertiaryContainer = AccentContainer,
    onTertiaryContainer = Color(0xFF002A26),
    error = SahmError,
    onError = Color.White,
    errorContainer = SahmErrorContainer,
    onErrorContainer = Color(0xFF410E0B),
    background = Neutral5,
    onBackground = Neutral95,
    surface = Elevation1,
    onSurface = Neutral95,
    surfaceVariant = Neutral20,
    onSurfaceVariant = Neutral60,
    surfaceContainer = Elevation1,
    surfaceContainerHigh = Elevation2,
    surfaceContainerHighest = Elevation3,
    outline = Neutral40,
    outlineVariant = Color(0xFFE0D3BF),
    scrim = Color(0xFF000000),
    inverseSurface = Neutral95,
    inverseOnSurface = Neutral5,
    inversePrimary = BrandPrimaryLight,
    surfaceTint = BrandPrimary,
)

val SahmDarkColors: ColorScheme = darkColorScheme(
    primary = BrandPrimaryLight,
    onPrimary = Color(0xFF3D2000),
    primaryContainer = BrandPrimaryDark,
    onPrimaryContainer = BrandPrimaryContainer,
    secondary = AccentTealLight,
    onSecondary = Color(0xFF003733),
    secondaryContainer = Color(0xFF004B43),
    onSecondaryContainer = AccentContainer,
    tertiary = AccentTealLight,
    onTertiary = Color(0xFF003733),
    error = Color(0xFFF2B8B5),
    background = Color(0xFF141210),
    onBackground = Color(0xFFE7E1DA),
    surface = Color(0xFF1E1B18),
    onSurface = Color(0xFFE7E1DA),
    surfaceVariant = Color(0xFF2E2924),
    onSurfaceVariant = Color(0xFFD4C4B8),
    outline = Color(0xFF9E8E82),
    outlineVariant = Color(0xFF4E4239),
    surfaceTint = BrandPrimaryLight,
)
