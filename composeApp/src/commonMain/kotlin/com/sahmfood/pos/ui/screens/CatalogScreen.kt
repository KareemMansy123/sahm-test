package com.sahmfood.pos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.presentation.catalog.CatalogIntent
import com.sahmfood.pos.presentation.catalog.CatalogStore
import com.sahmfood.pos.presentation.favorites.FavoritesIntent
import com.sahmfood.pos.presentation.favorites.FavoritesStore
import com.sahmfood.pos.ui.components.CategoryStrip
import com.sahmfood.pos.ui.components.PlazaAddressHeader
import com.sahmfood.pos.ui.components.PlazaHomeBanner
import com.sahmfood.pos.ui.components.PlazaSearchBar
import com.sahmfood.pos.ui.components.ProductCard
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryContainer
import com.sahmfood.pos.ui.theme.Neutral5
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmError
import com.sahmfood.pos.ui.theme.SahmSpacing

/**
 * Home tab content (no Scaffold — MainScreen owns the bottom nav).
 *
 * - PlazaAddressHeader (top)
 * - Notification bell (right of header)
 * - PlazaSearchBar
 * - PlazaHomeBanner (orange gradient promo)
 * - CategoryStrip (pastel circles)
 * - Product grid (LazyVerticalGrid, hearts wired to FavoritesStore)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    store: CatalogStore,
    favoritesStore: FavoritesStore,
    onOpenProduct: (Product) -> Unit,
) {
    val state by store.state.collectAsState()
    val favState by favoritesStore.state.collectAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral5),
    ) {
        val columns = when {
            maxWidth >= 840.dp -> 4
            maxWidth >= 600.dp -> 3
            else -> 2
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(
                start = SahmSpacing.lg,
                end = SahmSpacing.lg,
                bottom = SahmSpacing.xxxl,
            ),
            horizontalArrangement = Arrangement.spacedBy(SahmSpacing.md),
            verticalArrangement = Arrangement.spacedBy(SahmSpacing.md),
            modifier = Modifier.fillMaxSize(),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = SahmSpacing.sm)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        PlazaAddressHeader(modifier = Modifier.weight(1f))
                        NotificationBell(modifier = Modifier.padding(end = SahmSpacing.lg))
                    }
                    Spacer(Modifier.height(SahmSpacing.sm))
                    Box(modifier = Modifier.padding(horizontal = SahmSpacing.lg)) {
                        PlazaSearchBar(
                            value = state.searchQuery,
                            onValueChange = { store.dispatch(CatalogIntent.SetSearchQuery(it)) },
                        )
                    }
                    Spacer(Modifier.height(SahmSpacing.lg))
                    PlazaHomeBanner(
                        title = "Today's Special",
                        subtitle = "20% off combo meals · Express only",
                        ctaLabel = "Browse Combos",
                        onCtaClick = { store.dispatch(CatalogIntent.SelectCategory(null)) },
                    )
                    Spacer(Modifier.height(SahmSpacing.lg))
                    SectionHeader(title = "Shop by Category")
                    Spacer(Modifier.height(SahmSpacing.xs))
                    CategoryStrip(
                        categories = state.categories,
                        selected = state.selectedCategory,
                        onSelect = { store.dispatch(CatalogIntent.SelectCategory(it)) },
                    )
                    Spacer(Modifier.height(SahmSpacing.sm))
                    SectionHeader(title = state.selectedCategory ?: "All Items")
                    Spacer(Modifier.height(SahmSpacing.sm))
                }
            }
            if (state.isLoading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = BrandPrimary)
                    }
                }
            } else {
                items(items = state.filteredProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        isFavorite = product.id in favState.favoriteIds,
                        onCardTap = { onOpenProduct(product) },
                        onAdd = { store.dispatch(CatalogIntent.AddToCart(product)) },
                        onToggleFavorite = {
                            favoritesStore.dispatch(FavoritesIntent.Toggle(product.id))
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationBell(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(BrandPrimaryContainer, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Outlined.NotificationsNone,
            contentDescription = "Notifications",
            tint = BrandPrimary,
            modifier = Modifier.size(22.dp),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(8.dp)
                .background(SahmError, CircleShape),
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SahmSpacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = (-0.3).sp,
            ),
            color = Neutral95,
        )
    }
}
