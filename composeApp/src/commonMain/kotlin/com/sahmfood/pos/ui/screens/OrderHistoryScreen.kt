package com.sahmfood.pos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.OrderStatus
import com.sahmfood.pos.presentation.history.HistoryIntent
import com.sahmfood.pos.presentation.history.HistoryStore
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    store: HistoryStore,
    onBack: () -> Unit
) {
    val state by store.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SahmSpacing.lg),
                shape = RoundedCornerShape(SahmRadius.md),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(SahmSpacing.lg),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Orders", style = MaterialTheme.typography.labelMedium)
                        Text("${state.todayOrderCount}", style = MaterialTheme.typography.headlineMedium)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Revenue", style = MaterialTheme.typography.labelMedium)
                        Text(
                            Money(state.todayRevenue).toDisplayString(),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }

            if (state.orders.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "No orders yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(SahmSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(SahmSpacing.sm)
                ) {
                    items(items = state.orders, key = { it.id }) { order ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(SahmRadius.md),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SahmSpacing.lg),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Order #${order.id.takeLast(6).uppercase()}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        "${order.paymentMethod} · ${statusLabel(order.status)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    order.grandTotal.toDisplayString(),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun statusLabel(status: OrderStatus): String = when (status) {
    OrderStatus.DRAFT -> "Draft"
    OrderStatus.PAID -> "Paid (offline)"
    OrderStatus.SYNC_PENDING -> "Pending sync"
    OrderStatus.SYNCED -> "Synced"
    OrderStatus.SYNC_FAILED -> "Sync failed"
}
