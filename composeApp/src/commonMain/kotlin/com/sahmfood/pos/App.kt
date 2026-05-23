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
import com.sahmfood.pos.ui.screens.OrderTrackingScreen
import com.sahmfood.pos.ui.screens.ReceiptScreen
import com.sahmfood.pos.ui.theme.SahmTheme
import org.koin.compose.koinInject

sealed class Screen(val index: Int) {
    data object Catalog : Screen(0)
    data object Cart : Screen(1)
    data object Checkout : Screen(2)
    data object Receipt : Screen(3)
    data class OrderTracking(val orderId: String) : Screen(4)
    data object History : Screen(5)
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

        LaunchedEffect(Unit) {
            catalogSeed.seedIfEmpty()
            syncWorker.start()
        }

        // Catalog effect: navigate to checkout when user taps Charge.
        LaunchedEffect(catalogStore) {
            catalogStore.effects.collect { eff ->
                when (eff) {
                    is CatalogEffect.NavigateToCheckout -> {
                        checkoutStore.dispatch(CheckoutIntent.Initialize(eff.cart, eff.totals))
                        screen = Screen.Checkout
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
                val direction = if (targetState.index >= initialState.index) 1 else -1
                (slideInHorizontally(tween(280)) { it * direction } + fadeIn()) togetherWith
                    (slideOutHorizontally(tween(280)) { -it * direction / 3 } + fadeOut())
            },
            label = "screen-transition",
        ) { current ->
            when (current) {
                Screen.Catalog -> CatalogScreen(
                    store = catalogStore,
                    onOpenHistory = { screen = Screen.History },
                    onOpenCart = { screen = Screen.Cart },
                )
                Screen.Cart -> CartScreen(
                    store = catalogStore,
                    onBack = { screen = Screen.Catalog },
                    onCheckout = { catalogStore.dispatch(CatalogIntent.Checkout) },
                )
                Screen.Checkout -> CheckoutScreen(
                    store = checkoutStore,
                    onBack = { screen = Screen.Cart },
                    onPaymentComplete = { screen = Screen.Receipt },
                )
                Screen.Receipt -> ReceiptScreen(
                    store = checkoutStore,
                    onNewOrder = {
                        catalogStore.dispatch(CatalogIntent.ClearCart)
                        screen = Screen.Catalog
                    },
                    onTrackOrder = {
                        // The receipt screen is only visible after PaymentSucceeded,
                        // which guarantees completedOrder is non-null. If it is
                        // null we silently stay on the receipt rather than open a
                        // tracker for a junk "—" id.
                        val orderId = checkoutStore.state.value.completedOrder?.id
                        if (orderId != null) {
                            screen = Screen.OrderTracking(orderId)
                        }
                    },
                )
                is Screen.OrderTracking -> OrderTrackingScreen(
                    orderId = current.orderId,
                    onBack = {
                        catalogStore.dispatch(CatalogIntent.ClearCart)
                        screen = Screen.Catalog
                    },
                )
                Screen.History -> OrderHistoryScreen(
                    store = historyStore,
                    onBack = { screen = Screen.Catalog },
                )
            }
        }
    }
}
