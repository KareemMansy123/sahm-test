package com.sahmfood.pos

import androidx.compose.runtime.Composable

/**
 * Cross-platform wrapper around the platform's "system back" affordance.
 *
 * - On Android, delegates to `androidx.activity.compose.BackHandler`, which
 *   intercepts the hardware/gesture back button via `OnBackPressedDispatcher`.
 * - On iOS, this is a no-op — UIKit doesn't have a global "back" event.
 *   Each screen's own back button (in the top app bar) is the canonical
 *   way to navigate back on iOS, and the swipe-back gesture would require
 *   wrapping every screen in a `UINavigationController`, which we
 *   intentionally don't do (we own navigation in Compose).
 *
 * When [enabled] is false the platform handler must NOT consume the back
 * event, so the OS default (exit on Android) runs.
 */
@Composable
expect fun SystemBackHandler(enabled: Boolean, onBack: () -> Unit)
