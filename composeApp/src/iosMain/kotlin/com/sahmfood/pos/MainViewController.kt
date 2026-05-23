package com.sahmfood.pos

import androidx.compose.ui.window.ComposeUIViewController
import com.sahmfood.pos.di.initKoin
import platform.UIKit.UIViewController

/**
 * iOS entry point. Swift code calls [MainViewControllerKt.MainViewController]
 * to obtain a UIViewController hosting the Compose UI tree.
 *
 * Koin is initialized lazily on first call; iOS has no Application equivalent
 * that runs before SwiftUI/UIKit boot, so this is the earliest sensible spot.
 */
private var koinStarted = false

fun MainViewController(): UIViewController {
    if (!koinStarted) {
        initKoin()
        koinStarted = true
    }
    return ComposeUIViewController { App() }
}
