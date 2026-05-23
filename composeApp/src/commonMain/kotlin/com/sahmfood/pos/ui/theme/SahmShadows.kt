package com.sahmfood.pos.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Plaza-style shadow recipes. Plaza uses flat surfaces (`elevation: 0`)
 * with manual BoxShadows for the lift. Compose's `Modifier.shadow` is the
 * closest equivalent, but only ambient/spot colors give us control over
 * tint — alpha is approximated.
 */

/** Card resting shadow — black @ 4%, blur 8, offset y2. */
fun Modifier.plazaCardShadow(
    shape: Shape = RoundedCornerShape(12.dp),
    elevation: Dp = 2.dp,
): Modifier = shadow(
    elevation = elevation,
    shape = shape,
    ambientColor = Color.Black.copy(alpha = 0.04f),
    spotColor = Color.Black.copy(alpha = 0.06f),
)

/** Premium card shadow — black @ 5%, blur 10, offset y4. */
fun Modifier.plazaCardShadowRaised(
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: Dp = 4.dp,
): Modifier = shadow(
    elevation = elevation,
    shape = shape,
    ambientColor = Color.Black.copy(alpha = 0.05f),
    spotColor = Color.Black.copy(alpha = 0.08f),
)

/** Primary-tinted shadow used on brand CTAs and the floating cart FAB. */
fun Modifier.plazaBrandShadow(
    shape: Shape,
    elevation: Dp = 8.dp,
    color: Color = BrandPrimary,
): Modifier = shadow(
    elevation = elevation,
    shape = shape,
    ambientColor = color.copy(alpha = 0.25f),
    spotColor = color.copy(alpha = 0.35f),
)

/** Bottom-bar shadow (upward) — black @ 6%, blur 12, offset y-2. */
fun Modifier.plazaBottomBarShadow(
    shape: Shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
): Modifier = shadow(
    elevation = 6.dp,
    shape = shape,
    ambientColor = Color.Black.copy(alpha = 0.06f),
    spotColor = Color.Black.copy(alpha = 0.10f),
)
