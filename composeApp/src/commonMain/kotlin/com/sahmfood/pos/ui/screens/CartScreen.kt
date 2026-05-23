package com.sahmfood.pos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.presentation.catalog.CatalogIntent
import com.sahmfood.pos.presentation.catalog.CatalogStore
import com.sahmfood.pos.ui.components.CartLineItem
import com.sahmfood.pos.ui.components.EmptyCartState
import com.sahmfood.pos.ui.components.OrderTotalCard
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.Elevation2
import com.sahmfood.pos.ui.theme.Neutral5
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    store: CatalogStore,
    onBack: () -> Unit,
    onCheckout: () -> Unit,
) {
    val state by store.state.collectAsState()
    Scaffold(
        containerColor = Neutral5,
        topBar = {
            TopAppBar(
                title = {
                    Text("Your Order", style = MaterialTheme.typography.titleLarge, color = Neutral95)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBackIos,
                            contentDescription = "Back",
                            tint = Neutral95,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                actions = {
                    if (!state.isCartEmpty) {
                        TextButton(onClick = { store.dispatch(CatalogIntent.ClearCart) }) {
                            Text("Clear", color = BrandPrimary,
                                style = MaterialTheme.typography.labelLarge)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Elevation2)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isCartEmpty) {
                EmptyCartState(onBrowse = onBack)
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(
                            horizontal = SahmSpacing.lg,
                            vertical = SahmSpacing.md
                        ),
                        verticalArrangement = Arrangement.spacedBy(SahmSpacing.sm)
                    ) {
                        items(items = state.cart, key = { it.product.id }) { item ->
                            CartLineItem(
                                item = item,
                                onIncrement = {
                                    store.dispatch(CatalogIntent.UpdateQuantity(
                                        item.product.id, item.quantity + 1
                                    ))
                                },
                                onDecrement = {
                                    if (item.quantity > 1) {
                                        store.dispatch(CatalogIntent.UpdateQuantity(
                                            item.product.id, item.quantity - 1
                                        ))
                                    } else {
                                        store.dispatch(CatalogIntent.RemoveFromCart(item.product.id))
                                    }
                                }
                            )
                        }
                    }
                    // Pinned summary at the bottom
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Elevation2,
                        shape = RoundedCornerShape(
                            topStart = SahmRadius.lg,
                            topEnd = SahmRadius.lg
                        ),
                        tonalElevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(SahmSpacing.lg)) {
                            OrderTotalCard(
                                totals = state.totals,
                                onCharge = onCheckout,
                                chargeEnabled = !state.isCartEmpty,
                                chargeLabel = "Proceed to Checkout"
                            )
                            Spacer(Modifier.size(SahmSpacing.xs))
                        }
                    }
                }
            }
        }
    }
}
