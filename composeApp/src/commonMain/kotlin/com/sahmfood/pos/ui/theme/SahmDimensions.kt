package com.sahmfood.pos.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Spacing scale — reference app uses 16 as the dominant value with 4/8/12 for
 * tighter gaps and 20/24/32 for hero padding.
 */
object SahmSpacing {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp     // base — most common
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
    val huge = 48.dp
}

/**
 * Border radii — dimension scale.
 * 12 is the default everywhere; 16 for premium cards; 8 for small chips;
 * 24 for bottom-sheet top corners; full-round (50%+) for circular avatars.
 */
object SahmRadius {
    val xs = 4.dp     // tiny badges
    val sm = 8.dp     // search bar, snackbars, small chips
    val md = 12.dp    // default — buttons, cards, nav pills
    val lg = 16.dp    // premium cards (cart item, checkout section)
    val xl = 20.dp    // banner CTA, status badges
    val xxl = 24.dp   // bottom sheets, cart summary top
    val pill = 999.dp
}

object SahmElevation {
    val flat = 0.dp
    val card = 1.dp
    val raised = 4.dp
    val floating = 8.dp
    val modal = 16.dp
}

object SahmDimens {
    val minTouchTarget = 48.dp
    val primaryButtonHeight = 56.dp
    val ctaButtonHeight = 56.dp
    val keypadKeyHeight = 64.dp
    val appBarHeight = 64.dp
    val heroHeaderTablet = 200.dp
    val heroHeaderPhone = 160.dp
    val bannerHeight = 160.dp
    val searchBarHeight = 48.dp
    val bottomNavHeight = 72.dp
    val categoryCircleSize = 70.dp
    val productImageHeight = 120.dp   // card image
    val cartItemImageSize = 90.dp
    val cartFabSize = 60.dp
    val stepperButton = 36.dp
    val orderStepCircle = 56.dp
}

object SahmDurations {
    const val short = 150
    const val medium = 250
    const val long = 350
    const val extraLong = 500
    const val pageFade = 600
}
