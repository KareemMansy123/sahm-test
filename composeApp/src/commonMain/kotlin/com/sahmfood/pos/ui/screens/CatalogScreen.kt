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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.presentation.catalog.CatalogIntent
import com.sahmfood.pos.presentation.catalog.CatalogStore
import com.sahmfood.pos.ui.components.CategoryStrip
import com.sahmfood.pos.ui.components.PlazaAddressHeader
import com.sahmfood.pos.ui.components.PlazaFloatingCartFab
import com.sahmfood.pos.ui.components.PlazaHomeBanner
import com.sahmfood.pos.ui.components.PlazaSearchBar
import com.sahmfood.pos.ui.components.ProductCard
import com.sahmfood.pos.ui.components.ProductDetailSheet
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryContainer
import com.sahmfood.pos.ui.theme.Neutral5
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmError
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    store: CatalogStore,
    onOpenHistory: () -> Unit,
    onOpenCart: () -> Unit,
) {
    val state by store.state.collectAsState()
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                bottom = 96.dp, // clear floating FAB
            ),
            horizontalArrangement = Arrangement.spacedBy(SahmSpacing.md),
            verticalArrangement = Arrangement.spacedBy(SahmSpacing.md),
            modifier = Modifier.fillMaxSize(),
        ) {
            // Status bar spacer
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            }
            // Sticky-ish header content: address bar + search row + banner + category strip
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = SahmSpacing.sm)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        PlazaAddressHeader(modifier = Modifier.weight(1f))
                        // Notification bell
                        Box(
                            modifier = Modifier
                                .padding(end = SahmSpacing.lg)
                                .size(40.dp)
                                .clip(RoundedCornerShape(SahmRadius.sm))
                                .background(BrandPrimaryContainer),
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
                        onCtaClick = {
                            store.dispatch(CatalogIntent.SelectCategory(null))
                        },
                    )
                    Spacer(Modifier.height(SahmSpacing.lg))
                    SectionHeader(
                        title = "Shop by Category",
                        action = "Order History",
                        onAction = onOpenHistory,
                    )
                    Spacer(Modifier.height(SahmSpacing.xs))
                    CategoryStrip(
                        categories = state.categories,
                        selected = state.selectedCategory,
                        onSelect = { store.dispatch(CatalogIntent.SelectCategory(it)) },
                    )
                    Spacer(Modifier.height(SahmSpacing.sm))
                    SectionHeader(
                        title = state.selectedCategory ?: "All Items",
                        action = null,
                        onAction = null,
                    )
                    Spacer(Modifier.height(SahmSpacing.sm))
                }
            }
            // Loading state
            if (state.isLoading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = BrandPrimary)
                    }
                }
            } else {
                items(items = state.filteredProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onCardTap = { selectedProduct = product },
                        onAdd = { store.dispatch(CatalogIntent.AddToCart(product)) },
                    )
                }
            }
        }

        // Floating cart FAB
        if (state.cartItemCount > 0) {
            PlazaFloatingCartFab(
                count = state.cartItemCount,
                onClick = onOpenCart,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(SahmSpacing.lg),
            )
        }

        selectedProduct?.let { product ->
            ProductDetailSheet(
                product = product,
                sheetState = sheetState,
                onDismiss = { selectedProduct = null },
                onAdd = { qty ->
                    repeat(qty) { store.dispatch(CatalogIntent.AddToCart(product)) }
                    selectedProduct = null
                },
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    action: String?,
    onAction: (() -> Unit)?,
) {
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
            ),
            color = Neutral95,
        )
        if (action != null && onAction != null) {
            Surface(
                modifier = Modifier.clickable(onClick = onAction),
                shape = RoundedCornerShape(20.dp),
                color = BrandPrimaryContainer,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = SahmSpacing.md, vertical = 6.dp),
                ) {
                    Text(
                        action,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                        ),
                        color = BrandPrimary,
                    )
                }
            }
        }
    }
}

