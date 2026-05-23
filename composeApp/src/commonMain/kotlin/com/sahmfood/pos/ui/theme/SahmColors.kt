package com.sahmfood.pos.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Sahm Food POS palette — adopts the Plaza-app design language.
 * Plaza is a vibrant orange brand on a soft-grey neutral canvas.
 */

// Brand
val BrandPrimary = Color(0xFFFF6B35)
val BrandPrimaryDark = Color(0xFFE55A2B)
val BrandPrimaryLight = Color(0xFFFF8A5B)
val BrandOnPrimary = Color(0xFFFFFFFF)
val BrandPrimaryContainer = Color(0x1AFF6B35)  // primary @ 10%
val BrandOnPrimaryContainer = Color(0xFFE55A2B)

// Secondary — forest green (used on success CTAs, "Order Confirmed" buttons)
val SecondaryColor = Color(0xFF2E7D32)
val SecondaryColorLight = Color(0xFF4CAF50)

// Accents
val AccentCyan = Color(0xFF00BCD4)
val AccentGreen = Color(0xFF00C853)
val AccentBlue = Color(0xFF2196F3)
val AccentPurple = Color(0xFF8B5CF6)
val ExpressColor = Color(0xFF00BFA5)      // teal — express badge
val FreeDeliveryColor = Color(0xFF00C853)
val AccentTeal = Color(0xFF00BFA5)        // alias kept for back-compat

// Neutrals — Plaza's clean grey scale
val Neutral0 = Color(0xFFFFFFFF)
val Neutral5 = Color(0xFFF7F7F7)          // background
val Neutral10 = Color(0xFFF3F4F6)         // image placeholder bg
val Neutral20 = Color(0xFFE5E5E5)         // divider
val Neutral40 = Color(0xFFE0E0E0)         // border
val Neutral60 = Color(0xFF9CA3AF)         // hint / tertiary
val Neutral80 = Color(0xFF6B7280)         // secondary text
val Neutral95 = Color(0xFF1A1A1A)         // primary text

// Elevation surfaces — flat surfaces, shadow does the lifting
val Elevation0 = Color(0xFFF7F7F7)
val Elevation1 = Color(0xFFFFFFFF)
val Elevation2 = Color(0xFFFFFFFF)
val Elevation3 = Color(0xFFFFFFFF)

// Semantic
val SahmError = Color(0xFFEF4444)
val SahmErrorContainer = Color(0x1AEF4444)
val SahmSuccess = Color(0xFF22C55E)
val SahmWarning = Color(0xFFF59E0B)
val SahmInfo = Color(0xFF3B82F6)

// Rating
val RatingColor = Color(0xFFFFC107)
val RatingBackground = Color(0xFFFFF8E1)

// Price / discount
val PriceColor = Color(0xFF1A1A1A)
val OldPriceColor = Color(0xFF9CA3AF)
val DiscountColor = Color(0xFFEF4444)
val DealColor = Color(0xFF00C853)

// Shimmer
val ShimmerBase = Color(0xFFE0E0E0)
val ShimmerHighlight = Color(0xFFF5F5F5)

// Pastel cycle for category circles (Plaza pattern)
val CategoryPastels = listOf(
    Color(0xFFE8F5E9), // light green
    Color(0xFFFFF3E0), // light orange
    Color(0xFFE3F2FD), // light blue
    Color(0xFFFCE4EC), // light pink
    Color(0xFFF3E5F5), // light purple
    Color(0xFFE0F7FA), // light cyan
    Color(0xFFFFF8E1), // light amber
    Color(0xFFEFEBE9), // light brown
)

// Category placeholder gradients — kept for product hero images
val CatBurgers = listOf(Color(0xFFFF6B35), Color(0xFFE55A2B))
val CatPizza = listOf(Color(0xFFE53935), Color(0xFFB71C1C))
val CatDrinks = listOf(Color(0xFF2196F3), Color(0xFF1976D2))
val CatDesserts = listOf(Color(0xFF8B5CF6), Color(0xFF6A1B9A))
val CatSides = listOf(Color(0xFF66BB6A), Color(0xFF388E3C))
val CatDefault = listOf(BrandPrimary, BrandPrimaryDark)

fun categoryGradient(category: String): List<Color> = when (category.lowercase()) {
    "burgers" -> CatBurgers
    "pizza" -> CatPizza
    "drinks" -> CatDrinks
    "desserts" -> CatDesserts
    "sides" -> CatSides
    else -> CatDefault
}

fun categoryPastel(index: Int): Color = CategoryPastels[index.mod(CategoryPastels.size)]

val SahmLightColors: ColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = BrandOnPrimary,
    primaryContainer = BrandPrimaryContainer,
    onPrimaryContainer = BrandOnPrimaryContainer,
    secondary = SecondaryColor,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E9),
    onSecondaryContainer = Color(0xFF1B5E20),
    tertiary = ExpressColor,
    onTertiary = Color.White,
    tertiaryContainer = Color(0x1A00BFA5),
    onTertiaryContainer = Color(0xFF004D40),
    error = SahmError,
    onError = Color.White,
    errorContainer = SahmErrorContainer,
    onErrorContainer = Color(0xFF7F1D1D),
    background = Neutral5,
    onBackground = Neutral95,
    surface = Neutral0,
    onSurface = Neutral95,
    surfaceVariant = Neutral10,
    onSurfaceVariant = Neutral80,
    surfaceContainer = Neutral0,
    surfaceContainerHigh = Neutral0,
    surfaceContainerHighest = Neutral0,
    outline = Neutral40,
    outlineVariant = Neutral20,
    scrim = Color(0xFF000000),
    inverseSurface = Neutral95,
    inverseOnSurface = Neutral0,
    inversePrimary = BrandPrimaryLight,
    surfaceTint = BrandPrimary,
)

// Dark theme — Plaza doesn't ship one; we provide a sensible inversion.
val SahmDarkColors: ColorScheme = darkColorScheme(
    primary = BrandPrimaryLight,
    onPrimary = Color(0xFF3D1A00),
    primaryContainer = BrandPrimaryDark,
    onPrimaryContainer = Color(0xFFFFE0CC),
    secondary = SecondaryColorLight,
    onSecondary = Color(0xFF003300),
    tertiary = ExpressColor,
    onTertiary = Color.White,
    error = Color(0xFFFCA5A5),
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
