package com.sahmfood.pos

import androidx.compose.runtime.Composable

/**
 * iOS has no system back button — every back action comes from a tap on
 * the in-screen back arrow (or, in the future, a swipe-back gesture
 * recogniser attached to the host UIViewController). So this is a no-op.
 */
@Composable
actual fun SystemBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // intentionally empty
}
