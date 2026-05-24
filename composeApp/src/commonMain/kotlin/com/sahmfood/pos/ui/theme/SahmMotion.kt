package com.sahmfood.pos.ui.theme

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Centralised motion vocabulary. Every animation in the app should pull from
 * this file so durations and easings stay consistent. Sahm tone:
 * spring-y but never rubbery, never longer than ~400ms for state transitions.
 *
 * Three tiers of spec exist:
 *  - [SahmDurations] — raw integer ms tokens (already defined in SahmDimensions).
 *  - [SahmSprings] — physical springs for press / lift / pop interactions.
 *  - [Enter] / [Exit] — ready-to-use combined transitions for AnimatedVisibility.
 */

object SahmSprings {
    /** Snappy press feedback — settles quickly without overshoot. */
    fun <T> press(): FiniteAnimationSpec<T> =
        spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow)

    /** Bouncy pop — used for badges and the FAB pulse. */
    fun <T> pop(): FiniteAnimationSpec<T> =
        spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium)

    /** Smooth glide — for color / size transitions where overshoot would feel weird. */
    fun <T> glide(): FiniteAnimationSpec<T> =
        spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessLow)
}

/**
 * Ready-made enter transitions. Use directly with AnimatedVisibility or
 * AnimatedContent.
 *
 * `Enter.listItem(index)` returns a staggered fade+slide for an item at
 * position [index] in a list — successive items animate in 40ms apart.
 */
object Enter {
    val fade: EnterTransition = fadeIn(tween(SahmDurations.medium))

    val slideUp: EnterTransition =
        slideInVertically(tween(SahmDurations.medium)) { it / 6 } +
            fadeIn(tween(SahmDurations.medium))

    val slideDown: EnterTransition =
        slideInVertically(tween(SahmDurations.medium)) { -it / 6 } +
            fadeIn(tween(SahmDurations.medium))

    val slideLeft: EnterTransition =
        slideInHorizontally(tween(SahmDurations.medium)) { it / 4 } +
            fadeIn(tween(SahmDurations.medium))

    val slideRight: EnterTransition =
        slideInHorizontally(tween(SahmDurations.medium)) { -it / 4 } +
            fadeIn(tween(SahmDurations.medium))

    val pop: EnterTransition =
        scaleIn(spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium), 0.85f) +
            fadeIn(tween(SahmDurations.short))

    /**
     * Stagger helper. Index 0 starts at t=0, index 1 at t=40ms, etc. — capped
     * at 8 (320ms) so very long lists don't feel sleepy.
     */
    fun listItem(index: Int): EnterTransition {
        val delay = (index.coerceAtMost(8)) * 40
        return slideInVertically(
            animationSpec = tween(SahmDurations.medium, delayMillis = delay),
            initialOffsetY = { it / 8 },
        ) + fadeIn(tween(SahmDurations.medium, delayMillis = delay))
    }
}

object Exit {
    val fade: ExitTransition = fadeOut(tween(SahmDurations.short))

    val slideUp: ExitTransition =
        slideOutVertically(tween(SahmDurations.short)) { -it / 6 } +
            fadeOut(tween(SahmDurations.short))

    val slideDown: ExitTransition =
        slideOutVertically(tween(SahmDurations.short)) { it / 6 } +
            fadeOut(tween(SahmDurations.short))

    val pop: ExitTransition =
        scaleOut(spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium), 0.85f) +
            fadeOut(tween(SahmDurations.short))
}

/**
 * Adds button-style press feedback — the element scales down to 0.96 while
 * pressed and springs back on release. Doesn't intercept clicks (the caller
 * still wires `Modifier.clickable`); we only listen for press interactions.
 *
 * Usage:
 * ```
 * Box(modifier = Modifier
 *     .pressScale(interactionSource)
 *     .clickable(interactionSource = interactionSource, indication = null) { ... }
 * )
 * ```
 */
fun Modifier.pressScale(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.96f,
): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val target = if (pressed) pressedScale else 1f
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = target,
        animationSpec = SahmSprings.press(),
        label = "press-scale",
    )
    graphicsLayer { scaleX = scale; scaleY = scale }
}

/**
 * Convenience for `Modifier.pressScale` that creates and remembers the
 * interaction source for callers that don't need to share it.
 */
@Composable
fun Modifier.pressScaleAuto(pressedScale: Float = 0.96f): Modifier {
    val src = remember { MutableInteractionSource() }
    return this.pressScale(src, pressedScale)
}
