package com.sahmfood.pos.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Contactless
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.PaymentMethod
import com.sahmfood.pos.presentation.checkout.CheckoutEffect
import com.sahmfood.pos.presentation.checkout.CheckoutIntent
import com.sahmfood.pos.presentation.checkout.CheckoutStore
import com.sahmfood.pos.ui.components.CheckoutSectionCard
import com.sahmfood.pos.ui.components.OrderTotalCard
import com.sahmfood.pos.ui.components.PlazaPrimaryButton
import com.sahmfood.pos.ui.theme.AccentBlue
import com.sahmfood.pos.ui.theme.AccentTeal
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryContainer
import com.sahmfood.pos.ui.theme.Neutral10
import com.sahmfood.pos.ui.theme.Neutral20
import com.sahmfood.pos.ui.theme.Neutral40
import com.sahmfood.pos.ui.theme.Neutral5
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral80
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmError
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing
import com.sahmfood.pos.ui.theme.SecondaryColorLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    store: CheckoutStore,
    onBack: () -> Unit,
    onPaymentComplete: () -> Unit,
) {
    val state by store.state.collectAsState()

    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    LaunchedEffect(store) {
        store.effects.collect { eff ->
            if (eff is CheckoutEffect.PaymentSucceeded) onPaymentComplete()
        }
    }

    Scaffold(
        containerColor = Neutral5,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        strings.checkoutTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !state.isProcessing) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBackIos,
                            contentDescription = "Back",
                            tint = Neutral95,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(topStart = SahmRadius.xxl, topEnd = SahmRadius.xxl),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(topStart = SahmRadius.xxl, topEnd = SahmRadius.xxl),
                        ambientColor = Color.Black.copy(alpha = 0.08f),
                        spotColor = Color.Black.copy(alpha = 0.12f),
                    ),
            ) {
                Column(modifier = Modifier.padding(SahmSpacing.xl)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            strings.checkoutTotalAmount,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = Neutral80,
                        )
                        Text(
                            state.totals.grandTotal.toDisplayString(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                            ),
                            color = BrandPrimary,
                        )
                    }
                    Spacer(Modifier.height(SahmSpacing.md))
                    PlazaPrimaryButton(
                        text = if (state.paymentMethod == PaymentMethod.CASH) strings.checkoutConfirmCash
                               else strings.checkoutConfirmCard,
                        onClick = { store.dispatch(CheckoutIntent.ConfirmPayment) },
                        enabled = state.canConfirm && !state.isProcessing,
                    )
                }
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = SahmSpacing.lg,
                end = SahmSpacing.lg,
                top = SahmSpacing.md,
                bottom = SahmSpacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(SahmSpacing.lg),
        ) {
            item {
                CheckoutSectionCard(icon = Icons.Rounded.Receipt, title = strings.checkoutOrderSummary) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.items.forEach { ci ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    ci.product.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                    color = Neutral95,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    "x${ci.quantity}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                                    color = Neutral60,
                                    modifier = Modifier.padding(end = SahmSpacing.sm),
                                )
                                Text(
                                    ci.lineTotal.toDisplayString(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                    color = Neutral95,
                                )
                            }
                        }
                        Spacer(Modifier.height(SahmSpacing.xs))
                        HorizontalDivider(color = Neutral20, thickness = 1.dp)
                        Spacer(Modifier.height(SahmSpacing.xs))
                        OrderTotalCard(
                            totals = state.totals,
                            onCharge = {},
                            chargeEnabled = false,
                            showButton = false,
                        )
                    }
                }
            }
            item {
                CheckoutSectionCard(icon = Icons.Rounded.LocationOn, title = strings.checkoutCounter) {
                    Column {
                        Text(
                            strings.checkoutCounterValue,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                            ),
                            color = Neutral95,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            strings.checkoutCounterLocation,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = Neutral60,
                        )
                    }
                }
            }
            item {
                CheckoutSectionCard(icon = Icons.Rounded.Payments, title = strings.checkoutPaymentMethod) {
                    Column(verticalArrangement = Arrangement.spacedBy(SahmSpacing.md)) {
                        PaymentMethodOption(
                            icon = Icons.Rounded.AttachMoney,
                            iconColor = SecondaryColorLight,
                            title = strings.checkoutCash,
                            subtitle = strings.checkoutCashDescription,
                            selected = state.paymentMethod == PaymentMethod.CASH,
                            onClick = { store.dispatch(CheckoutIntent.SetPaymentMethod(PaymentMethod.CASH)) },
                        )
                        PaymentMethodOption(
                            icon = Icons.Rounded.CreditCard,
                            iconColor = AccentBlue,
                            title = strings.checkoutCard,
                            subtitle = strings.checkoutCardDescription,
                            selected = state.paymentMethod == PaymentMethod.CARD,
                            onClick = { store.dispatch(CheckoutIntent.SetPaymentMethod(PaymentMethod.CARD)) },
                        )
                    }
                }
            }
            // Card-tap simulator only — cash needs no extra UI; the bottom
            // bar's Confirm button is enough for a face-to-face cash sale.
            if (state.paymentMethod == PaymentMethod.CARD) {
                item { CardTenderCard() }
            }
            item {
                CheckoutSectionCard(icon = Icons.Rounded.Notes, title = strings.checkoutOrderNotes) {
                    Text(
                        strings.checkoutOrderNotesHint,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = Neutral60,
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodOption(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(SahmRadius.lg),
        color = if (selected) iconColor.copy(alpha = 0.10f) else Neutral10,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) iconColor else Neutral40,
        ),
    ) {
        Row(
            modifier = Modifier.padding(SahmSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (selected) iconColor.copy(alpha = 0.20f) else Neutral20,
                        RoundedCornerShape(SahmRadius.md),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (selected) iconColor else Neutral80,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.width(SahmSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    ),
                    color = Neutral95,
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = Neutral60,
                )
            }
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(
                        width = if (selected) 0.dp else 2.dp,
                        color = if (selected) iconColor else Neutral60,
                        shape = CircleShape,
                    )
                    .background(if (selected) iconColor else Color.Transparent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (selected) {
                    Box(modifier = Modifier.size(10.dp).background(Color.White, CircleShape))
                }
            }
        }
    }
}

@Composable
private fun CardTenderCard() {
    val infinite = rememberInfiniteTransition(label = "card-pulse")
    val scale by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.20f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "scale",
    )
    val alpha by infinite.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "alpha",
    )

    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    CheckoutSectionCard(icon = Icons.Rounded.Contactless, title = strings.checkoutTapCardTitle) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = SahmSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
                        .background(BrandPrimary, CircleShape),
                )
                Surface(
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    color = BrandPrimaryContainer,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Rounded.Contactless,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(44.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.height(SahmSpacing.lg))
            Text(
                strings.checkoutTapCardHint,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = Neutral80,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(SahmSpacing.md))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(AccentTeal, CircleShape))
                Spacer(Modifier.width(6.dp))
                Text(
                    strings.checkoutTerminalReady,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    ),
                    color = AccentTeal,
                )
            }
        }
    }
}

