package com.sahmfood.pos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.sahmfood.pos.ui.components.PlazaEmptyState
import com.sahmfood.pos.ui.components.ProductCard
import com.sahmfood.pos.ui.theme.Neutral5
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmSpacing

/**
 * Full-screen list of products in one category, pushed from the
 * Categories tab. Top bar has a back arrow that returns to the
 * Categories grid (no tab change).
 *
 * A null [category] means "All Items" — show the full catalog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductsScreen(
    category: String?,
    catalogStore: CatalogStore,
    favoritesStore: FavoritesStore,
    onBack: () -> Unit,
    onOpenProduct: (Product) -> Unit,
) {
    val state by catalogStore.state.collectAsState()
    val favState by favoritesStore.state.collectAsState()
    val products = remember(state.products, category) {
        if (category == null) state.products
        else state.products.filter { it.category == category }
    }

    Scaffold(
        containerColor = Neutral5,
        topBar = {
            TopAppBar(
                title = {
                    androidx.compose.foundation.layout.Column {
                        Text(
                            category ?: "All Items",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold, fontSize = 18.sp),
                            color = Neutral95,
                        )
                        if (products.isNotEmpty()) {
                            Text(
                                if (products.size == 1) "1 item" else "${products.size} items",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = Neutral60,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBackIos,
                            contentDescription = "Back to categories",
                            tint = Neutral95,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Neutral5)) {
            if (products.isEmpty()) {
                PlazaEmptyState(
                    icon = Icons.Rounded.ShoppingBag,
                    title = "No items in $category",
                    description = "Add products to this category from the catalog.",
                )
            } else {
                BoxWithConstraints {
                    val columns = when {
                        maxWidth >= 840.dp -> 4
                        maxWidth >= 600.dp -> 3
                        else -> 2
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        contentPadding = PaddingValues(SahmSpacing.lg),
                        horizontalArrangement = Arrangement.spacedBy(SahmSpacing.md),
                        verticalArrangement = Arrangement.spacedBy(SahmSpacing.md),
                    ) {
                        items(products, key = { it.id }) { product ->
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
}
