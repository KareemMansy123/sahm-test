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
import com.sahmfood.pos.data.seed.MenuSeedData
import com.sahmfood.pos.data.sync.SyncWorker
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.domain.usecases.SeedCatalogIfNeeded
import com.sahmfood.pos.presentation.ai.AiChatStore
import com.sahmfood.pos.presentation.catalog.CatalogEffect
import com.sahmfood.pos.presentation.catalog.CatalogIntent
import com.sahmfood.pos.presentation.catalog.CatalogStore
import com.sahmfood.pos.presentation.checkout.CheckoutIntent
import com.sahmfood.pos.presentation.checkout.CheckoutStore
import com.sahmfood.pos.presentation.favorites.FavoritesStore
import com.sahmfood.pos.presentation.history.HistoryEffect
import com.sahmfood.pos.presentation.history.HistoryStore
import com.sahmfood.pos.presentation.settings.AppSettingsStore
import com.sahmfood.pos.ui.screens.AiChatScreen
import com.sahmfood.pos.ui.screens.CheckoutScreen
import com.sahmfood.pos.ui.screens.FavoritesScreen
import com.sahmfood.pos.ui.screens.HelpSupportScreen
import com.sahmfood.pos.ui.screens.MainScreen
import com.sahmfood.pos.ui.screens.OrderTrackingScreen
import com.sahmfood.pos.ui.screens.PreferencesScreen
import com.sahmfood.pos.ui.screens.PrinterSettingsScreen
import com.sahmfood.pos.ui.screens.ProductDetailScreen
import com.sahmfood.pos.ui.screens.ReceiptScreen
import com.sahmfood.pos.ui.screens.SwitchRegisterScreen
import com.sahmfood.pos.ui.theme.SahmTheme
import org.koin.compose.koinInject

sealed class Route(val depth: Int) {
    data object Main : Route(0)
    data class ProductDetail(val product: Product) : Route(1)
    data object Favorites : Route(1)
    data object SwitchRegister : Route(1)
    data object PrinterSettings : Route(1)
    data object Preferences : Route(1)
    data object Help : Route(1)
    data object Checkout : Route(2)
    data object Receipt : Route(3)
    data class Tracking(val orderId: String) : Route(4)
    data object AiChat : Route(5)
}

@Composable
fun App() {
    SahmTheme {
        val catalogStore: CatalogStore = koinInject()
        val checkoutStore: CheckoutStore = koinInject()
        val historyStore: HistoryStore = koinInject()
        val favoritesStore: FavoritesStore = koinInject()
        val aiChatStore: AiChatStore = koinInject()
        val settings: AppSettingsStore = koinInject()
        val seedCatalogIfNeeded: SeedCatalogIfNeeded = koinInject()
        val syncWorker: SyncWorker = koinInject()

        var route by remember { mutableStateOf<Route>(Route.Main) }

        LaunchedEffect(Unit) {
            seedCatalogIfNeeded(MenuSeedData.MENU)
            syncWorker.start()
        }

        LaunchedEffect(catalogStore) {
            catalogStore.effects.collect { eff ->
                if (eff is CatalogEffect.NavigateToCheckout) {
                    checkoutStore.dispatch(CheckoutIntent.Initialize(eff.cart, eff.totals))
                    route = Route.Checkout
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
                favoritesStore.cancel()
                aiChatStore.cancel()
            }
        }

        AnimatedContent(
            targetState = route,
            transitionSpec = {
                val direction = if (targetState.depth >= initialState.depth) 1 else -1
                (slideInHorizontally(tween(280)) { it * direction } + fadeIn()) togetherWith
                    (slideOutHorizontally(tween(280)) { -it * direction / 3 } + fadeOut())
            },
            label = "route",
        ) { current ->
            when (current) {
                Route.Main -> MainScreen(
                    catalogStore = catalogStore,
                    favoritesStore = favoritesStore,
                    historyStore = historyStore,
                    settings = settings,
                    onOpenProduct = { product -> route = Route.ProductDetail(product) },
                    onOpenCheckout = { catalogStore.dispatch(CatalogIntent.Checkout) },
                    onOpenAi = { route = Route.AiChat },
                    onOpenFavorites = { route = Route.Favorites },
                    onOpenSwitchRegister = { route = Route.SwitchRegister },
                    onOpenPrinterSettings = { route = Route.PrinterSettings },
                    onOpenPreferences = { route = Route.Preferences },
                    onOpenHelp = { route = Route.Help },
                )
                is Route.ProductDetail -> ProductDetailScreen(
                    product = current.product,
                    favoritesStore = favoritesStore,
                    onBack = { route = Route.Main },
                    onAddToCart = { qty ->
                        repeat(qty) {
                            catalogStore.dispatch(CatalogIntent.AddToCart(current.product))
                        }
                        route = Route.Main
                    },
                )
                Route.Favorites -> FavoritesScreen(
                    favoritesStore = favoritesStore,
                    catalogStore = catalogStore,
                    onOpenProduct = { product -> route = Route.ProductDetail(product) },
                    onBrowseMenu = { route = Route.Main },
                )
                Route.SwitchRegister -> SwitchRegisterScreen(onBack = { route = Route.Main })
                Route.PrinterSettings -> PrinterSettingsScreen(onBack = { route = Route.Main })
                Route.Preferences -> PreferencesScreen(onBack = { route = Route.Main })
                Route.Help -> HelpSupportScreen(onBack = { route = Route.Main })
                Route.Checkout -> CheckoutScreen(
                    store = checkoutStore,
                    onBack = { route = Route.Main },
                    onPaymentComplete = { route = Route.Receipt },
                )
                Route.Receipt -> ReceiptScreen(
                    store = checkoutStore,
                    onNewOrder = {
                        catalogStore.dispatch(CatalogIntent.ClearCart)
                        route = Route.Main
                    },
                    onTrackOrder = {
                        val orderId = checkoutStore.state.value.completedOrder?.id
                        if (orderId != null) route = Route.Tracking(orderId)
                    },
                )
                is Route.Tracking -> OrderTrackingScreen(
                    orderId = current.orderId,
                    onBack = {
                        catalogStore.dispatch(CatalogIntent.ClearCart)
                        route = Route.Main
                    },
                )
                Route.AiChat -> AiChatScreen(
                    store = aiChatStore,
                    onBack = { route = Route.Main },
                )
            }
        }
    }
}

// FavoritesScreen takes a separate callback. The list-mode cards inside
// already navigate to detail / add / remove via the store.
