package com.sahmfood.pos.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.ShoppingBag
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.presentation.catalog.CatalogIntent
import com.sahmfood.pos.presentation.catalog.CatalogStore
import com.sahmfood.pos.ui.components.CartLineItem
import com.sahmfood.pos.ui.components.OrderTotalCard
import com.sahmfood.pos.ui.components.PlazaEmptyState
import com.sahmfood.pos.ui.theme.BrandPrimary
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
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    Scaffold(
        containerColor = Neutral5,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        strings.cartTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        ),
                        color = Neutral95,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBackIos,
                            contentDescription = "Back",
                            tint = Neutral95,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                },
                actions = {
                    if (!state.isCartEmpty) {
                        TextButton(onClick = { store.dispatch(CatalogIntent.ClearCart) }) {
                            Text(
                                strings.cartClear,
                                color = BrandPrimary,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                ),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isCartEmpty) {
                PlazaEmptyState(
                    icon = Icons.Rounded.ShoppingBag,
                    title = strings.cartEmptyTitle,
                    description = strings.cartEmptyDescription,
                    ctaLabel = strings.cartEmptyCta,
                    onCta = onBack,
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(
                            horizontal = SahmSpacing.lg,
                            vertical = SahmSpacing.md,
                        ),
                        verticalArrangement = Arrangement.spacedBy(SahmSpacing.md),
                    ) {
                        items(items = state.cart, key = { it.product.id }) { item ->
                            CartLineItem(
                                modifier = Modifier.animateItem(
                                    fadeInSpec = tween(220),
                                    fadeOutSpec = tween(180),
                                    placementSpec = androidx.compose.animation.core.spring(
                                        dampingRatio = 0.8f,
                                        stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow,
                                    ),
                                ),
                                item = item,
                                onIncrement = {
                                    store.dispatch(
                                        CatalogIntent.UpdateQuantity(
                                            item.product.id, item.quantity + 1,
                                        ),
                                    )
                                },
                                onDecrement = {
                                    if (item.quantity > 1) {
                                        store.dispatch(
                                            CatalogIntent.UpdateQuantity(
                                                item.product.id, item.quantity - 1,
                                            ),
                                        )
                                    } else {
                                        store.dispatch(
                                            CatalogIntent.RemoveFromCart(item.product.id),
                                        )
                                    }
                                },
                                onRemove = {
                                    store.dispatch(CatalogIntent.RemoveFromCart(item.product.id))
                                },
                            )
                        }
                    }
                    // Pinned bottom summary
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 16.dp,
                                shape = RoundedCornerShape(
                                    topStart = SahmRadius.xxl,
                                    topEnd = SahmRadius.xxl,
                                ),
                                ambientColor = Color.Black.copy(alpha = 0.08f),
                                spotColor = Color.Black.copy(alpha = 0.12f),
                            ),
                        color = Color.White,
                        shape = RoundedCornerShape(
                            topStart = SahmRadius.xxl,
                            topEnd = SahmRadius.xxl,
                        ),
                    ) {
                        Box(modifier = Modifier.padding(SahmSpacing.xl)) {
                            OrderTotalCard(
                                totals = state.totals,
                                onCharge = onCheckout,
                                chargeEnabled = !state.isCartEmpty,
                                chargeLabel = strings.cartProceedToCheckout,
                            )
                        }
                    }
                }
            }
        }
    }
}
