package com.sahmfood.pos

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.sahmfood.pos.ui.screens.CatalogScreen
import com.sahmfood.pos.ui.screens.CheckoutScreen
import com.sahmfood.pos.ui.screens.OrderHistoryScreen
import com.sahmfood.pos.ui.screens.ReceiptScreen
import com.sahmfood.pos.ui.theme.SahmTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

sealed class Screen {
    data object Catalog : Screen()
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
        val scope = rememberCoroutineScope()

        var screen by remember { mutableStateOf<Screen>(Screen.Catalog) }

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
                        screen = Screen.Checkout
                    }
                    else -> Unit
                }
            }
        }

        // Surface any history-store errors via println; production would route to a Snackbar host.
        LaunchedEffect(historyStore) {
            historyStore.effects.collect { eff ->
                if (eff is HistoryEffect.ShowError) {
                    println("[history] ${eff.message}")
                }
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
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screen-transition"
        ) { current ->
            when (current) {
                Screen.Catalog -> CatalogScreen(
                    store = catalogStore,
                    onOpenHistory = { screen = Screen.History }
                )
                Screen.Checkout -> CheckoutScreen(
                    store = checkoutStore,
                    onBack = { screen = Screen.Catalog },
                    onPaymentComplete = { screen = Screen.Receipt }
                )
                Screen.Receipt -> ReceiptScreen(
                    store = checkoutStore,
                    onNewOrder = {
                        catalogStore.dispatch(CatalogIntent.ClearCart)
                        screen = Screen.Catalog
                    }
                )
                Screen.History -> OrderHistoryScreen(
                    store = historyStore,
                    onBack = { screen = Screen.Catalog }
                )
            }
        }
    }
}
