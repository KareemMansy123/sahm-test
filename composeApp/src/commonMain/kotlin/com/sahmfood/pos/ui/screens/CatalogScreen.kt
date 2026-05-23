package com.sahmfood.pos.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.domain.entities.CartItem
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.presentation.catalog.CatalogIntent
import com.sahmfood.pos.presentation.catalog.CatalogState
import com.sahmfood.pos.presentation.catalog.CatalogStore
import com.sahmfood.pos.ui.components.CartLineItem
import com.sahmfood.pos.ui.components.CategoryChipRow
import com.sahmfood.pos.ui.components.EmptyCartState
import com.sahmfood.pos.ui.components.OrderTotalCard
import com.sahmfood.pos.ui.components.ProductCard
import com.sahmfood.pos.ui.theme.SahmDimens
import com.sahmfood.pos.ui.theme.SahmSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    store: CatalogStore,
    onOpenHistory: () -> Unit
) {
    val state by store.state.collectAsState()
    var showCartSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Sahm Food", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = onOpenHistory) {
                        Icon(Icons.Default.History, contentDescription = "Order history")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        BoxWithConstraints(modifier = Modifier.padding(padding).fillMaxSize()) {
            val isExpanded = maxWidth >= 840.dp
            if (isExpanded) {
                Row(modifier = Modifier.fillMaxSize()) {
                    CatalogLeftPane(
                        state = state,
                        onIntent = store::dispatch,
                        modifier = Modifier.weight(0.6f).fillMaxHeight()
                    )
                    Surface(
                        modifier = Modifier.weight(0.4f).fillMaxHeight(),
                        tonalElevation = 2.dp,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        CartPane(state = state, onIntent = store::dispatch)
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    CatalogLeftPane(
                        state = state,
                        onIntent = store::dispatch,
                        modifier = Modifier.fillMaxSize()
                    )
                    BadgedBox(
                        badge = {
                            if (state.cartItemCount > 0) {
                                Badge { Text(state.cartItemCount.toString()) }
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(SahmSpacing.lg)
                    ) {
                        FloatingActionButton(
                            onClick = { showCartSheet = true },
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Open cart")
                        }
                    }
                    if (showCartSheet) {
                        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
                        ModalBottomSheet(
                            onDismissRequest = { showCartSheet = false },
                            sheetState = sheetState
                        ) {
                            CartPane(state = state, onIntent = store::dispatch)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogLeftPane(
    state: CatalogState,
    onIntent: (CatalogIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        CategoryChipRow(
            categories = state.categories,
            selected = state.selectedCategory,
            onSelect = { onIntent(CatalogIntent.SelectCategory(it)) }
        )
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { onIntent(CatalogIntent.SetSearchQuery(it)) },
            placeholder = { Text("Search items...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SahmSpacing.lg, vertical = SahmSpacing.sm),
            shape = CircleShape,
            singleLine = true
        )
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 200.dp),
                contentPadding = PaddingValues(SahmSpacing.lg),
                horizontalArrangement = Arrangement.spacedBy(SahmSpacing.md),
                verticalArrangement = Arrangement.spacedBy(SahmSpacing.md),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = state.filteredProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onAdd = { onIntent(CatalogIntent.AddToCart(product)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CartPane(
    state: CatalogState,
    onIntent: (CatalogIntent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Current Order",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(SahmSpacing.lg)
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        if (state.isCartEmpty) {
            EmptyCartState(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items = state.cart, key = { it.product.id }) { item ->
                    CartLineItem(
                        item = item,
                        onIncrement = {
                            onIntent(CatalogIntent.UpdateQuantity(item.product.id, item.quantity + 1))
                        },
                        onDecrement = {
                            onIntent(CatalogIntent.UpdateQuantity(item.product.id, item.quantity - 1))
                        },
                        onRemove = { onIntent(CatalogIntent.RemoveFromCart(item.product.id)) }
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(horizontal = SahmSpacing.lg)
                    )
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Column(modifier = Modifier.padding(SahmSpacing.lg)) {
            OrderTotalCard(
                totals = state.totals,
                onCharge = { onIntent(CatalogIntent.Checkout) },
                chargeEnabled = !state.isCartEmpty
            )
        }
    }
}
