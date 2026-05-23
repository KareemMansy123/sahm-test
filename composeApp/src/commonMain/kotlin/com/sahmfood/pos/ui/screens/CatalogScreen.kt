package com.sahmfood.pos.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.presentation.catalog.CatalogIntent
import com.sahmfood.pos.presentation.catalog.CatalogStore
import com.sahmfood.pos.ui.components.CategoryStrip
import com.sahmfood.pos.ui.components.FloatingSearchBar
import com.sahmfood.pos.ui.components.HeroHeader
import com.sahmfood.pos.ui.components.ProductCard
import com.sahmfood.pos.ui.components.ProductDetailSheet
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.Neutral5
import com.sahmfood.pos.ui.theme.SahmSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    store: CatalogStore,
    onOpenHistory: () -> Unit,
    onOpenCart: () -> Unit
) {
    val state by store.state.collectAsState()
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral5)
    ) {
        val isTablet = maxWidth >= 600.dp
        val columns = when {
            maxWidth >= 840.dp -> 4
            maxWidth >= 600.dp -> 3
            else -> 2
        }

        // Hero header
        HeroHeader(isTablet = isTablet)

        // Top-right corner action icons (history, cart)
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = SahmSpacing.lg, end = SahmSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(SahmSpacing.sm)
        ) {
            HeaderActionButton(
                icon = Icons.Rounded.History,
                contentDescription = "Order history",
                onClick = onOpenHistory
            )
            CartHeaderButton(count = state.cartItemCount, onClick = onOpenCart)
        }

        // Scrollable content
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.height(if (isTablet) 154.dp else 134.dp))
            // Floating search bar — overlaps the header bottom
            FloatingSearchBar(
                value = state.searchQuery,
                onValueChange = { store.dispatch(CatalogIntent.SetSearchQuery(it)) },
                modifier = Modifier.padding(horizontal = SahmSpacing.xl)
            )
            Spacer(Modifier.height(SahmSpacing.lg))
            CategoryStrip(
                categories = state.categories,
                selected = state.selectedCategory,
                onSelect = { store.dispatch(CatalogIntent.SelectCategory(it)) }
            )
            Spacer(Modifier.height(SahmSpacing.sm))

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.CircularProgressIndicator(color = BrandPrimary)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(
                        start = SahmSpacing.lg,
                        end = SahmSpacing.lg,
                        bottom = SahmSpacing.xxxl
                    ),
                    horizontalArrangement = Arrangement.spacedBy(SahmSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(SahmSpacing.md),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items = state.filteredProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            onCardTap = { selectedProduct = product },
                            onAdd = { store.dispatch(CatalogIntent.AddToCart(product)) }
                        )
                    }
                }
            }
        }

        // Product detail bottom sheet
        selectedProduct?.let { product ->
            ProductDetailSheet(
                product = product,
                sheetState = sheetState,
                onDismiss = { selectedProduct = null },
                onAdd = { qty ->
                    repeat(qty) { store.dispatch(CatalogIntent.AddToCart(product)) }
                    selectedProduct = null
                }
            )
        }
    }
}

@Composable
private fun HeaderActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(44.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.18f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icon,
                contentDescription = contentDescription,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun CartHeaderButton(count: Int, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (count > 0) 1f else 1f,
        animationSpec = spring(dampingRatio = 0.3f, stiffness = Spring.StiffnessHigh),
        label = "cart-pulse"
    )
    // Trigger a pulse each time count changes by remembering count and animating.
    var lastCount by remember { mutableStateOf(count) }
    val pulse by animateFloatAsState(
        targetValue = if (count != lastCount) 1.25f else 1f,
        animationSpec = spring(dampingRatio = 0.3f, stiffness = Spring.StiffnessHigh),
        label = "cart-bounce"
    )
    LaunchedEffect(count) {
        if (count != lastCount) {
            lastCount = count
        }
    }
    BadgedBox(
        badge = {
            AnimatedVisibility(
                visible = count > 0,
                enter = scaleIn(spring(dampingRatio = 0.4f)) + fadeIn(),
                exit = fadeOut()
            ) {
                Badge(containerColor = MaterialTheme.colorScheme.error) {
                    Text(count.toString(), color = Color.White)
                }
            }
        },
        modifier = Modifier.graphicsLayer { scaleX = pulse; scaleY = pulse }
    ) {
        Surface(
            modifier = Modifier
                .size(44.dp)
                .clickable(onClick = onClick),
            shape = CircleShape,
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Rounded.ShoppingBag,
                    contentDescription = "Open cart",
                    tint = BrandPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
