package com.sahmfood.pos.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.presentation.catalog.CatalogIntent
import com.sahmfood.pos.presentation.catalog.CatalogStore
import com.sahmfood.pos.presentation.favorites.FavoritesIntent
import com.sahmfood.pos.presentation.favorites.FavoritesStore
import com.sahmfood.pos.presentation.history.HistoryStore
import com.sahmfood.pos.presentation.settings.AppSettingsStore
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingBag
import com.sahmfood.pos.ui.components.AiFloatingButton
import com.sahmfood.pos.ui.components.SahmBottomNav
import com.sahmfood.pos.ui.components.SahmBottomTabs
import com.sahmfood.pos.ui.components.SahmEmptyState
import com.sahmfood.pos.ui.components.ProductCard
import com.sahmfood.pos.ui.theme.Neutral5
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmSpacing
import kotlinx.coroutines.launch

/**
 * Sahm MainScreen scaffold.
 *
 * HorizontalPager body (5 tabs, user-scroll disabled — navigation is
 * tap-only via the bottom nav) + custom bottom nav + AI floating button raised
 * above the nav.
 *
 * The Cart and Orders tabs delegate to the CartScreen / OrderHistoryScreen
 * composables but stripped of their top bars since this scaffold provides
 * tab context.
 */
@Composable
fun MainScreen(
    catalogStore: CatalogStore,
    favoritesStore: FavoritesStore,
    historyStore: HistoryStore,
    settings: AppSettingsStore,
    /**
     * Index of the tab to show. Hoisted to the caller so MainScreen can be
     * disposed and recreated by the navigation host (when the user opens a
     * deeper route like ProductDetail) without resetting back to Home.
     */
    initialTabIndex: Int,
    /** Called whenever the user changes tabs. The caller persists this so
     *  the next time MainScreen mounts it lands on the right tab. */
    onTabChange: (Int) -> Unit,
    onOpenProduct: (Product) -> Unit,
    onOpenCheckout: () -> Unit,
    onOpenAi: () -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenCategory: (String?) -> Unit,
    onOpenSwitchRegister: () -> Unit,
    onOpenPrinterSettings: () -> Unit,
    onOpenPreferences: () -> Unit,
    onOpenHelp: () -> Unit,
) {
    val catalogState by catalogStore.state.collectAsState()
    val pagerState = rememberPagerState(
        initialPage = initialTabIndex.coerceIn(0, SahmBottomTabs.lastIndex),
        pageCount = { SahmBottomTabs.size },
    )
    val scope = rememberCoroutineScope()
    var selectedTabKey by remember(initialTabIndex) {
        mutableStateOf(SahmBottomTabs[initialTabIndex.coerceIn(0, SahmBottomTabs.lastIndex)].key)
    }

    LaunchedEffect(pagerState.currentPage) {
        selectedTabKey = SahmBottomTabs[pagerState.currentPage].key
        onTabChange(pagerState.currentPage)
    }

    Box(modifier = Modifier.fillMaxSize().background(Neutral5)) {
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                userScrollEnabled = false,
            ) { page ->
                when (SahmBottomTabs[page].key) {
                    "home" -> CatalogScreen(
                        store = catalogStore,
                        favoritesStore = favoritesStore,
                        onOpenProduct = onOpenProduct,
                    )
                    "cart" -> CartTabBody(
                        catalogStore = catalogStore,
                        onCheckout = onOpenCheckout,
                        onBrowseMenu = {
                            scope.launch { pagerState.animateScrollToPage(0, animationSpec = tween(420)) }
                        },
                    )
                    "menu" -> CategoriesTabBody(
                        catalogStore = catalogStore,
                        onCategoryPicked = { category -> onOpenCategory(category) },
                    )
                    "orders" -> OrdersTabBody(historyStore = historyStore)
                    "profile" -> ProfileScreen(
                        settings = settings,
                        onOpenFavorites = onOpenFavorites,
                        onOpenAi = onOpenAi,
                        onOpenSwitchRegister = onOpenSwitchRegister,
                        onOpenPrinterSettings = onOpenPrinterSettings,
                        onOpenPreferences = onOpenPreferences,
                        onOpenHelp = onOpenHelp,
                    )
                }
            }
            SahmBottomNav(
                selectedKey = selectedTabKey,
                cartCount = catalogState.cartItemCount,
                onSelect = { key ->
                    val idx = SahmBottomTabs.indexOfFirst { it.key == key }
                    if (idx >= 0) {
                        scope.launch { pagerState.animateScrollToPage(idx, animationSpec = tween(420)) }
                    }
                },
            )
        }
        AiFloatingButton(
            onClick = onOpenAi,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = SahmSpacing.lg, bottom = 96.dp),
        )
    }
}

@Composable
private fun CartTabBody(
    catalogStore: CatalogStore,
    onCheckout: () -> Unit,
    onBrowseMenu: () -> Unit,
) {
    // Reuse the full CartScreen but it owns its own top bar / scaffold.
    CartScreen(
        store = catalogStore,
        onBack = onBrowseMenu,
        onCheckout = onCheckout,
    )
}

/**
 * The Categories tab — a grid of large pastel category cards.
 * Tapping a category applies the filter on the catalog store and
 * navigates back to the Home tab to show the filtered products.
 */
@Composable
private fun CategoriesTabBody(
    catalogStore: CatalogStore,
    onCategoryPicked: (String?) -> Unit,
) {
    val state by catalogStore.state.collectAsState()
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    val lang = com.sahmfood.pos.ui.strings.currentLanguageCode()
    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(Neutral5)) {
        val columns = when {
            maxWidth >= 840.dp -> 4
            maxWidth >= 600.dp -> 3
            else -> 2
        }
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Text(
                strings.categoriesTitle,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold, fontSize = 22.sp),
                color = Neutral95,
                modifier = Modifier.padding(SahmSpacing.lg),
            )
            Text(
                strings.categoriesSubtitle,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                color = com.sahmfood.pos.ui.theme.Neutral60,
                modifier = Modifier.padding(horizontal = SahmSpacing.lg),
            )
            Spacer(Modifier.padding(top = SahmSpacing.md))
            if (state.categories.isEmpty()) {
                SahmEmptyState(
                    icon = Icons.Rounded.ShoppingBag,
                    title = strings.categoriesEmptyTitle,
                    description = strings.categoriesEmptyDescription,
                )
            } else {
                val cards = listOf<Pair<String?, Int>>(null to -1) +
                    state.categories.mapIndexed { i, c -> c to i }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(
                        start = SahmSpacing.lg, end = SahmSpacing.lg,
                        bottom = SahmSpacing.xxxl,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(SahmSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(SahmSpacing.md),
                ) {
                    items(cards, key = { (cat, _) -> cat ?: "_all" }) { (category, idx) ->
                        // Resolve the Arabic category label if needed by sampling a product.
                        val label = if (category == null) {
                            strings.categoriesAllItems
                        } else {
                            state.products.firstOrNull { it.category == category }
                                ?.localizedCategory(lang) ?: category
                        }
                        com.sahmfood.pos.ui.components.CategoryGridCard(
                            label = label,
                            category = category,
                            pastelIndex = if (idx < 0) state.categories.size else idx,
                            itemCount = if (category == null) state.products.size
                                        else state.products.count { it.category == category },
                            onClick = { onCategoryPicked(category) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrdersTabBody(historyStore: HistoryStore) {
    // Reuse OrderHistoryScreen. It has its own Scaffold; that's fine inside a tab.
    OrderHistoryScreen(store = historyStore, onBack = { /* tab — no back */ })
}
