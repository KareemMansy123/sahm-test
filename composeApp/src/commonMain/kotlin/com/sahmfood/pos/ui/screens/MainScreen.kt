package com.sahmfood.pos.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingBag
import com.sahmfood.pos.ui.components.AiFloatingButton
import com.sahmfood.pos.ui.components.PlazaBottomNav
import com.sahmfood.pos.ui.components.PlazaBottomTabs
import com.sahmfood.pos.ui.components.PlazaEmptyState
import com.sahmfood.pos.ui.components.ProductCard
import com.sahmfood.pos.ui.theme.Neutral5
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmSpacing
import kotlinx.coroutines.launch

/**
 * Plaza-style MainScreen scaffold.
 *
 * HorizontalPager body (5 tabs, user-scroll disabled to mirror Plaza's
 * tap-only navigation) + custom bottom nav + AI floating button raised
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
    onOpenProduct: (Product) -> Unit,
    onOpenCheckout: () -> Unit,
    onOpenAi: () -> Unit,
) {
    val catalogState by catalogStore.state.collectAsState()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { PlazaBottomTabs.size })
    val scope = rememberCoroutineScope()
    var selectedTabKey by remember { mutableStateOf(PlazaBottomTabs.first().key) }

    LaunchedEffect(pagerState.currentPage) {
        selectedTabKey = PlazaBottomTabs[pagerState.currentPage].key
    }

    Box(modifier = Modifier.fillMaxSize().background(Neutral5)) {
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                userScrollEnabled = false,
            ) { page ->
                when (PlazaBottomTabs[page].key) {
                    "home" -> CatalogScreen(
                        store = catalogStore,
                        favoritesStore = favoritesStore,
                        onOpenProduct = onOpenProduct,
                    )
                    "cart" -> CartTabBody(
                        catalogStore = catalogStore,
                        onCheckout = onOpenCheckout,
                        onBrowseMenu = {
                            scope.launch { pagerState.animateScrollToPage(0) }
                        },
                    )
                    "menu" -> MenuTabBody(
                        catalogStore = catalogStore,
                        favoritesStore = favoritesStore,
                        onOpenProduct = onOpenProduct,
                    )
                    "orders" -> OrdersTabBody(historyStore = historyStore)
                    "profile" -> ProfileScreen(
                        onOpenFavorites = {
                            // future: deep-link to favorites screen
                            scope.launch { pagerState.animateScrollToPage(0) }
                        },
                        onOpenAi = onOpenAi,
                    )
                }
            }
            PlazaBottomNav(
                selectedKey = selectedTabKey,
                cartCount = catalogState.cartItemCount,
                onSelect = { key ->
                    val idx = PlazaBottomTabs.indexOfFirst { it.key == key }
                    if (idx >= 0) {
                        scope.launch { pagerState.animateScrollToPage(idx) }
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

@Composable
private fun MenuTabBody(
    catalogStore: CatalogStore,
    favoritesStore: FavoritesStore,
    onOpenProduct: (Product) -> Unit,
) {
    val state by catalogStore.state.collectAsState()
    val favState by favoritesStore.state.collectAsState()
    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(Neutral5)) {
        val columns = when {
            maxWidth >= 840.dp -> 4
            maxWidth >= 600.dp -> 3
            else -> 2
        }
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Text(
                "All Items",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold, fontSize = 22.sp),
                color = Neutral95,
                modifier = Modifier.padding(SahmSpacing.lg),
            )
            if (state.products.isEmpty()) {
                PlazaEmptyState(
                    icon = Icons.Rounded.ShoppingBag,
                    title = "No items",
                    description = "Catalog is empty.",
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(
                        start = SahmSpacing.lg, end = SahmSpacing.lg,
                        bottom = SahmSpacing.xxxl,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(SahmSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(SahmSpacing.md),
                ) {
                    items(state.products, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            isFavorite = product.id in favState.favoriteIds,
                            onCardTap = { onOpenProduct(product) },
                            onAdd = { catalogStore.dispatch(CatalogIntent.AddToCart(product)) },
                            onToggleFavorite = {
                                favoritesStore.dispatch(FavoritesIntent.Toggle(product.id))
                            },
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
