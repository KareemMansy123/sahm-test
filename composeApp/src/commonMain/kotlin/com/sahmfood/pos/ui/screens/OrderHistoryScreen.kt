package com.sahmfood.pos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderStatus
import com.sahmfood.pos.domain.entities.PaymentMethod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.sahmfood.pos.ui.theme.AccentTeal
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryContainer
import com.sahmfood.pos.ui.theme.Elevation1
import com.sahmfood.pos.ui.theme.Elevation2
import com.sahmfood.pos.ui.theme.Neutral10
import com.sahmfood.pos.ui.theme.Neutral40
import com.sahmfood.pos.ui.theme.Neutral5
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral80
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing
import com.sahmfood.pos.ui.theme.SahmWarning
import com.sahmfood.pos.presentation.history.HistoryStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    store: HistoryStore,
    onBack: () -> Unit,
) {
    val state by store.state.collectAsState()
    var filter by remember { mutableStateOf("All") }

    val filtered = remember(state.orders, filter) {
        when (filter) {
            "Cash" -> state.orders.filter { it.paymentMethod == PaymentMethod.CASH }
            "Card" -> state.orders.filter { it.paymentMethod == PaymentMethod.CARD }
            "Synced" -> state.orders.filter { it.status == OrderStatus.SYNCED }
            "Pending" -> state.orders.filter { it.status == OrderStatus.SYNC_PENDING || it.status == OrderStatus.PAID }
            else -> state.orders
        }
    }

    Scaffold(
        containerColor = Neutral5,
        topBar = {
            TopAppBar(
                title = { Text("Order History", style = MaterialTheme.typography.titleLarge) },
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Elevation2)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Top stat banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SahmSpacing.lg),
                horizontalArrangement = Arrangement.spacedBy(SahmSpacing.md)
            ) {
                StatCard(
                    icon = Icons.Rounded.Receipt,
                    label = "Orders Today",
                    value = state.todayOrderCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Rounded.AccountBalanceWallet,
                    label = "Revenue Today",
                    value = Money(state.todayRevenue).toDisplayString(),
                    modifier = Modifier.weight(1f)
                )
            }

            // Filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = SahmSpacing.lg),
                horizontalArrangement = Arrangement.spacedBy(SahmSpacing.sm)
            ) {
                items(listOf("All", "Cash", "Card", "Synced", "Pending")) { name ->
                    FilterPill(
                        label = name,
                        selected = filter == name,
                        onClick = { filter = name }
                    )
                }
            }
            Spacer(Modifier.height(SahmSpacing.md))

            // Order list
            if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Rounded.Receipt,
                            contentDescription = null,
                            tint = Neutral40,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(Modifier.height(SahmSpacing.md))
                        Text("No orders yet",
                            style = MaterialTheme.typography.titleMedium, color = Neutral60)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = SahmSpacing.lg,
                        end = SahmSpacing.lg,
                        bottom = SahmSpacing.xxl
                    ),
                    verticalArrangement = Arrangement.spacedBy(SahmSpacing.sm)
                ) {
                    items(items = filtered, key = { it.id }) { order ->
                        OrderHistoryCard(order)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(SahmRadius.md),
        color = BrandPrimaryContainer
    ) {
        Row(
            modifier = Modifier.padding(SahmSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(BrandPrimary.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = BrandPrimary,
                    modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(SahmSpacing.md))
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium, color = Neutral60)
                Text(value, style = MaterialTheme.typography.titleMedium, color = Neutral95)
            }
        }
    }
}

@Composable
private fun FilterPill(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .height(36.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (selected) BrandPrimary else Neutral10,
        border = if (selected) null
                 else androidx.compose.foundation.BorderStroke(1.dp, Neutral40)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = SahmSpacing.lg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) Color.White else Neutral60
            )
        }
    }
}

@Composable
private fun OrderHistoryCard(order: Order) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SahmRadius.md),
        color = Elevation1,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(SahmSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val avatarColor = when (order.paymentMethod) {
                PaymentMethod.CASH -> SahmWarning
                PaymentMethod.CARD -> BrandPrimary
            }
            Box(
                modifier = Modifier.size(44.dp).background(avatarColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (order.paymentMethod == PaymentMethod.CASH) Icons.Rounded.Payments
                    else Icons.Rounded.CreditCard,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(SahmSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Order #${order.id.takeLast(6).uppercase()}",
                    style = MaterialTheme.typography.titleSmall,
                    color = Neutral95
                )
                Text(
                    "${order.paymentMethod.name} · ${formatTimestamp(order.createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral60
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    order.grandTotal.toDisplayString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = Neutral95
                )
                Spacer(Modifier.height(2.dp))
                SyncBadge(status = order.status)
            }
        }
    }
}

@Composable
private fun SyncBadge(status: OrderStatus) {
    val (icon, color, label) = when (status) {
        OrderStatus.SYNCED -> Triple(Icons.Rounded.CloudDone, AccentTeal, "Synced")
        OrderStatus.SYNC_PENDING, OrderStatus.PAID ->
            Triple(Icons.Rounded.CloudQueue, SahmWarning, "Pending")
        OrderStatus.SYNC_FAILED -> Triple(Icons.Rounded.CloudOff, MaterialTheme.colorScheme.error, "Failed")
        OrderStatus.DRAFT -> Triple(Icons.Rounded.CloudOff, Neutral60, "Draft")
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
        Spacer(Modifier.width(2.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

private fun formatTimestamp(epochMs: Long): String {
    val instant: Instant = Instant.fromEpochMilliseconds(epochMs)
    val tz: TimeZone = TimeZone.currentSystemDefault()
    val local: LocalDateTime = instant.toLocalDateTime(tz)
    val h = local.hour.toString().padStart(2, '0')
    val m = local.minute.toString().padStart(2, '0')
    return "$h:$m"
}
