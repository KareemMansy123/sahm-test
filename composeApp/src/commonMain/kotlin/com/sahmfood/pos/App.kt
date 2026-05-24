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
import androidx.compose.runtime.mutableStateListOf
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
import com.sahmfood.pos.ui.screens.CategoryProductsScreen
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

/**
 * Navigation routes.
 *
 * [depth] is used by the AnimatedContent direction logic — higher-depth
 * destinations slide in from the right, lower-depth (back) destinations
 * slide in from the left. It does NOT drive the back stack; the stack
 * is a real list, so popping returns to wherever you actually came from
 * regardless of depth ordering.
 */
sealed class Route(val depth: Int) {
    data object Main : Route(0)
    data class CategoryProducts(val category: String?) : Route(1)
    data object Favorites : Route(1)
    data object SwitchRegister : Route(1)
    data object PrinterSettings : Route(1)
    data object Preferences : Route(1)
    data object Help : Route(1)
    data object AiChat : Route(1)
    data class ProductDetail(val product: Product) : Route(2)
    data object Checkout : Route(2)
    data object Receipt : Route(3)
    data class Tracking(val orderId: String) : Route(4)
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

        // Real back stack — never empty (Main is the floor). `push(r)`
        // appends; `pop()` removes the last entry (or no-ops if we're
        // already at Main). The visible route is always `last()`.
        val backstack = remember { mutableStateListOf<Route>(Route.Main) }
        val current = backstack.last()

        fun push(next: Route) {
            // Avoid pushing the same destination twice in a row (defends
            // against double taps).
            if (backstack.last() != next) backstack.add(next)
        }

        fun pop() {
            if (backstack.size > 1) backstack.removeAt(backstack.lastIndex)
        }

        // System back button — pop the stack instead of finishing the app.
        // Wired via expect/actual: on Android this maps to OnBackPressedDispatcher,
        // on iOS it's a no-op (iOS uses swipe-back gestures handled per-screen).
        // When the stack is at Main (size == 1) the handler is disabled so the
        // system-default behaviour (exit on Android) kicks in.
        SystemBackHandler(enabled = backstack.size > 1) { pop() }

        LaunchedEffect(Unit) {
            seedCatalogIfNeeded(MenuSeedData.MENU)
            syncWorker.start()
        }

        LaunchedEffect(catalogStore) {
            catalogStore.effects.collect { eff ->
                if (eff is CatalogEffect.NavigateToCheckout) {
                    checkoutStore.dispatch(CheckoutIntent.Initialize(eff.cart, eff.totals))
                    push(Route.Checkout)
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
            targetState = current,
            transitionSpec = {
                val direction = if (targetState.depth >= initialState.depth) 1 else -1
                (slideInHorizontally(tween(280)) { it * direction } + fadeIn()) togetherWith
                    (slideOutHorizontally(tween(280)) { -it * direction / 3 } + fadeOut())
            },
            label = "route",
        ) { visible ->
            when (visible) {
                Route.Main -> MainScreen(
                    catalogStore = catalogStore,
                    favoritesStore = favoritesStore,
                    historyStore = historyStore,
                    settings = settings,
                    onOpenProduct = { product -> push(Route.ProductDetail(product)) },
                    onOpenCheckout = { catalogStore.dispatch(CatalogIntent.Checkout) },
                    onOpenAi = { push(Route.AiChat) },
                    onOpenFavorites = { push(Route.Favorites) },
                    onOpenCategory = { category -> push(Route.CategoryProducts(category)) },
                    onOpenSwitchRegister = { push(Route.SwitchRegister) },
                    onOpenPrinterSettings = { push(Route.PrinterSettings) },
                    onOpenPreferences = { push(Route.Preferences) },
                    onOpenHelp = { push(Route.Help) },
                )
                is Route.ProductDetail -> ProductDetailScreen(
                    product = visible.product,
                    favoritesStore = favoritesStore,
                    onBack = { pop() },
                    onAddToCart = { qty ->
                        repeat(qty) {
                            catalogStore.dispatch(CatalogIntent.AddToCart(visible.product))
                        }
                        pop()
                    },
                )
                is Route.CategoryProducts -> CategoryProductsScreen(
                    category = visible.category,
                    catalogStore = catalogStore,
                    favoritesStore = favoritesStore,
                    onBack = { pop() },
                    onOpenProduct = { product -> push(Route.ProductDetail(product)) },
                )
                Route.Favorites -> FavoritesScreen(
                    favoritesStore = favoritesStore,
                    catalogStore = catalogStore,
                    onOpenProduct = { product -> push(Route.ProductDetail(product)) },
                    onBrowseMenu = {
                        // Browse-menu is "leave Favorites, go to the catalog
                        // home tab" — that's an intentional jump to root,
                        // not a back-pop. Clear the stack to Main.
                        backstack.clear()
                        backstack.add(Route.Main)
                    },
                )
                Route.SwitchRegister -> SwitchRegisterScreen(onBack = { pop() })
                Route.PrinterSettings -> PrinterSettingsScreen(onBack = { pop() })
                Route.Preferences -> PreferencesScreen(onBack = { pop() })
                Route.Help -> HelpSupportScreen(onBack = { pop() })
                Route.Checkout -> CheckoutScreen(
                    store = checkoutStore,
                    onBack = { pop() },
                    onPaymentComplete = { push(Route.Receipt) },
                )
                Route.Receipt -> ReceiptScreen(
                    store = checkoutStore,
                    onNewOrder = {
                        // Receipt is a terminal screen — "new order" wipes
                        // the cart AND the checkout history so back-pressing
                        // from a future screen never lands you back on a
                        // stale receipt.
                        catalogStore.dispatch(CatalogIntent.ClearCart)
                        backstack.clear()
                        backstack.add(Route.Main)
                    },
                    onTrackOrder = {
                        val orderId = checkoutStore.state.value.completedOrder?.id
                        if (orderId != null) push(Route.Tracking(orderId))
                    },
                )
                is Route.Tracking -> OrderTrackingScreen(
                    orderId = visible.orderId,
                    onBack = {
                        // Tracking is also terminal for the order flow —
                        // pressing back returns to a fresh Main, not the
                        // stale receipt screen above us in the stack.
                        catalogStore.dispatch(CatalogIntent.ClearCart)
                        backstack.clear()
                        backstack.add(Route.Main)
                    },
                )
                Route.AiChat -> AiChatScreen(
                    store = aiChatStore,
                    onBack = { pop() },
                )
            }
        }
    }
}
