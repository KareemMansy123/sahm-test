package com.sahmfood.pos

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sahmfood.pos.data.seed.CatalogSeed
import com.sahmfood.pos.data.sync.SyncWorker
import com.sahmfood.pos.presentation.catalog.CatalogEffect
import com.sahmfood.pos.presentation.catalog.CatalogIntent
import com.sahmfood.pos.presentation.catalog.CatalogStore
import com.sahmfood.pos.presentation.checkout.CheckoutIntent
import com.sahmfood.pos.presentation.checkout.CheckoutStore
import com.sahmfood.pos.presentation.history.HistoryEffect
import com.sahmfood.pos.presentation.history.HistoryStore
import com.sahmfood.pos.ui.screens.CartScreen
import com.sahmfood.pos.ui.screens.CatalogScreen
import com.sahmfood.pos.ui.screens.CheckoutScreen
import com.sahmfood.pos.ui.screens.OrderHistoryScreen
import com.sahmfood.pos.ui.screens.ReceiptScreen
import com.sahmfood.pos.ui.theme.SahmTheme
import org.koin.compose.koinInject

sealed class Screen {
    data object Catalog : Screen()
    data object Cart : Screen()
    data object Checkout : Screen()
    data object Receipt : Screen()
    data object History : Screen()
}

@Composable
fun App() {
    SahmTheme {
        val catalogStore: CatalogStore = koinInject()
        val checkoutStore: CheckoutStore = koinInject()
        val historyStore: HistoryStore = koinInject()
        val catalogSeed: CatalogSeed = koinInject()
        val syncWorker: SyncWorker = koinInject()

        var screen by remember { mutableStateOf<Screen>(Screen.Catalog) }
        var screenIndex by remember { mutableStateOf(0) }

        // Map screens to navigation indices so transitions know which direction to slide.
        fun indexOf(s: Screen): Int = when (s) {
            Screen.Catalog -> 0
            Screen.Cart -> 1
            Screen.Checkout -> 2
            Screen.Receipt -> 3
            Screen.History -> 4
        }

        fun goTo(s: Screen) {
            screenIndex = indexOf(s)
            screen = s
        }

        LaunchedEffect(Unit) {
            catalogSeed.seedIfEmpty()
            syncWorker.start()
        }

        // Wire CatalogEffect.NavigateToCheckout → CheckoutStore.Initialize → Screen change.
        LaunchedEffect(catalogStore) {
            catalogStore.effects.collect { eff ->
                when (eff) {
                    is CatalogEffect.NavigateToCheckout -> {
                        checkoutStore.dispatch(CheckoutIntent.Initialize(eff.cart, eff.totals))
                        goTo(Screen.Checkout)
                    }
                    else -> Unit
                }
            }
        }

        LaunchedEffect(historyStore) {
            historyStore.effects.collect { eff ->
                if (eff is HistoryEffect.ShowError) println("[history] ${eff.message}")
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                catalogStore.cancel()
                checkoutStore.cancel()
                historyStore.cancel()
            }
        }

        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                val from = indexOf(initialState)
                val to = indexOf(targetState)
                val direction = if (to >= from) 1 else -1
                (slideInHorizontally(animationSpec = tween(250)) { it * direction } + fadeIn()) togetherWith
                    (slideOutHorizontally(animationSpec = tween(250)) { -it * direction / 3 } + fadeOut())
            },
            label = "screen-transition"
        ) { current ->
            when (current) {
                Screen.Catalog -> CatalogScreen(
                    store = catalogStore,
                    onOpenHistory = { goTo(Screen.History) },
                    onOpenCart = { goTo(Screen.Cart) }
                )
                Screen.Cart -> CartScreen(
                    store = catalogStore,
                    onBack = { goTo(Screen.Catalog) },
                    onCheckout = { catalogStore.dispatch(CatalogIntent.Checkout) }
                )
                Screen.Checkout -> CheckoutScreen(
                    store = checkoutStore,
                    onBack = { goTo(Screen.Cart) },
                    onPaymentComplete = { goTo(Screen.Receipt) }
                )
                Screen.Receipt -> ReceiptScreen(
                    store = checkoutStore,
                    onNewOrder = {
                        catalogStore.dispatch(CatalogIntent.ClearCart)
                        goTo(Screen.Catalog)
                    }
                )
                Screen.History -> OrderHistoryScreen(
                    store = historyStore,
                    onBack = { goTo(Screen.Catalog) }
                )
            }
        }
    }
}
