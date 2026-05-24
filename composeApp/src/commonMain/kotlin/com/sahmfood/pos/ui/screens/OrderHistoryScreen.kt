package com.sahmfood.pos.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.rounded.ReceiptLong
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderStatus
import com.sahmfood.pos.domain.entities.PaymentMethod
import com.sahmfood.pos.presentation.history.HistoryStore
import com.sahmfood.pos.ui.components.SahmEmptyState
import com.sahmfood.pos.ui.theme.AccentBlue
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryContainer
import com.sahmfood.pos.ui.theme.Neutral20
import com.sahmfood.pos.ui.theme.Neutral40
import com.sahmfood.pos.ui.theme.Neutral5
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmError
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing
import com.sahmfood.pos.ui.theme.SahmSuccess
import com.sahmfood.pos.ui.theme.SahmWarning
import com.sahmfood.pos.ui.theme.SecondaryColorLight
import com.sahmfood.pos.ui.theme.sahmCardShadow
import com.sahmfood.pos.ui.theme.pressScaleAuto
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    store: HistoryStore,
    onBack: () -> Unit,
) {
    val state by store.state.collectAsState()
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    var filterKey by remember { mutableStateOf("all") }

    val filtered = remember(state.orders, filterKey) {
        when (filterKey) {
            "cash" -> state.orders.filter { it.paymentMethod == PaymentMethod.CASH }
            "card" -> state.orders.filter { it.paymentMethod == PaymentMethod.CARD }
            "synced" -> state.orders.filter { it.status == OrderStatus.SYNCED }
            "pending" -> state.orders.filter {
                it.status == OrderStatus.SYNC_PENDING || it.status == OrderStatus.PAID
            }
            else -> state.orders
        }
    }

    Scaffold(
        containerColor = Neutral5,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        strings.historyTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBackIos,
                            contentDescription = strings.commonBack,
                            tint = Neutral95,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Stats banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SahmSpacing.lg),
                horizontalArrangement = Arrangement.spacedBy(SahmSpacing.md),
            ) {
                StatCard(
                    icon = Icons.Rounded.Receipt,
                    label = strings.historyOrdersToday,
                    value = state.todayOrderCount.toString(),
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    icon = Icons.Rounded.AccountBalanceWallet,
                    label = strings.historyRevenueToday,
                    value = Money(state.todayRevenue).toDisplayString(),
                    modifier = Modifier.weight(1f),
                )
            }
            // Filter chips
            val filterOptions = listOf(
                "all" to strings.historyFilterAll,
                "cash" to strings.historyFilterCash,
                "card" to strings.historyFilterCard,
                "synced" to strings.historyFilterSynced,
                "pending" to strings.historyFilterPending,
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = SahmSpacing.lg),
                horizontalArrangement = Arrangement.spacedBy(SahmSpacing.sm),
            ) {
                items(filterOptions) { (key, label) ->
                    FilterPill(
                        label = label,
                        selected = filterKey == key,
                        onClick = { filterKey = key },
                    )
                }
            }
            Spacer(Modifier.height(SahmSpacing.md))
            // Order list
            if (filtered.isEmpty()) {
                SahmEmptyState(
                    icon = Icons.Rounded.ReceiptLong,
                    title = strings.historyEmpty,
                    description = strings.historyEmpty,
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = SahmSpacing.lg,
                        end = SahmSpacing.lg,
                        bottom = SahmSpacing.xxl,
                    ),
                    verticalArrangement = Arrangement.spacedBy(SahmSpacing.sm),
                ) {
                    items(items = filtered, key = { it.id }) { order ->
                        OrderHistoryCard(
                            order,
                            modifier = Modifier.animateItem(
                                fadeInSpec = androidx.compose.animation.core.tween(220),
                                fadeOutSpec = androidx.compose.animation.core.tween(180),
                                placementSpec = androidx.compose.animation.core.spring(
                                    dampingRatio = 0.8f,
                                    stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow,
                                ),
                            ),
                        )
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
        color = BrandPrimaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(SahmSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(BrandPrimary.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.width(SahmSpacing.md))
            Column {
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                    color = Neutral60,
                )
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    ),
                    color = Neutral95,
                )
            }
        }
    }
}

@Composable
private fun FilterPill(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg by androidx.compose.animation.animateColorAsState(
        targetValue = if (selected) BrandPrimary else Color.White,
        animationSpec = androidx.compose.animation.core.tween(220),
        label = "pill-bg",
    )
    val textColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (selected) Color.White else Neutral60,
        animationSpec = androidx.compose.animation.core.tween(220),
        label = "pill-text",
    )
    Surface(
        modifier = Modifier
            .height(36.dp)
            .pressScaleAuto(pressedScale = 0.93f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = bg,
        border = if (selected) null
                 else BorderStroke(1.dp, Neutral40),
    ) {
        Box(
            modifier = Modifier.padding(horizontal = SahmSpacing.lg),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                ),
                color = textColor,
            )
        }
    }
}

@Composable
private fun OrderHistoryCard(order: Order, modifier: Modifier = Modifier) {
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .sahmCardShadow(shape = RoundedCornerShape(SahmRadius.md), elevation = 2.dp),
        shape = RoundedCornerShape(SahmRadius.md),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier.padding(SahmSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val avatarColor = when (order.paymentMethod) {
                PaymentMethod.CASH -> SecondaryColorLight
                PaymentMethod.CARD -> AccentBlue
            }
            Box(
                modifier = Modifier.size(44.dp).background(avatarColor, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (order.paymentMethod == PaymentMethod.CASH) Icons.Rounded.Payments
                    else Icons.Rounded.CreditCard,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(SahmSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    strings.historyOrderHashPrefix + order.id.takeLast(6).uppercase(),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    ),
                    color = Neutral95,
                )
                Text(
                    "${order.paymentMethod.name} · ${formatTimestamp(order.createdAt)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Neutral60,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    order.grandTotal.toDisplayString(),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    ),
                    color = Neutral95,
                )
                Spacer(Modifier.height(2.dp))
                SyncBadge(status = order.status)
            }
        }
    }
}

@Composable
private fun SyncBadge(status: OrderStatus) {
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    val (icon, color, label) = when (status) {
        OrderStatus.SYNCED -> Triple(Icons.Rounded.CloudDone, SahmSuccess, strings.historyStatusSynced)
        OrderStatus.SYNC_PENDING, OrderStatus.PAID ->
            Triple(Icons.Rounded.CloudQueue, SahmWarning, strings.historyStatusPending)
        OrderStatus.SYNC_FAILED -> Triple(Icons.Rounded.CloudOff, SahmError, strings.historyStatusFailed)
        OrderStatus.DRAFT -> Triple(Icons.Rounded.CloudOff, Neutral60, strings.historyStatusDraft)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp),
        )
        Spacer(Modifier.width(2.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = color,
        )
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
