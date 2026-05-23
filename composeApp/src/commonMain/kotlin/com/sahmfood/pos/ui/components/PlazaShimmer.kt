package com.sahmfood.pos.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.sahmfood.pos.ui.theme.ShimmerBase
import com.sahmfood.pos.ui.theme.ShimmerHighlight

/**
 * Shimmer modifier. Apply on a sized Box / Surface; the gradient sweeps
 * horizontally across the surface to indicate loading state. Mirrors the
 * `shimmer` package used in Plaza-app.
 */
fun Modifier.shimmer(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = -800f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "translateX",
    )
    background(
        brush = Brush.linearGradient(
            colors = listOf(ShimmerBase, ShimmerHighlight, ShimmerBase),
            start = Offset(translateX, 0f),
            end = Offset(translateX + 400f, 0f),
        ),
    )
}
