package com.sahmfood.pos.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Contactless
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Nfc
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.PaymentMethod
import com.sahmfood.pos.presentation.checkout.CheckoutEffect
import com.sahmfood.pos.presentation.checkout.CheckoutIntent
import com.sahmfood.pos.presentation.checkout.CheckoutStore
import com.sahmfood.pos.ui.components.CheckoutStepProgress
import com.sahmfood.pos.ui.components.PrimaryGradientButton
import com.sahmfood.pos.ui.theme.AccentTeal
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryContainer
import com.sahmfood.pos.ui.theme.Elevation1
import com.sahmfood.pos.ui.theme.Elevation2
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    store: CheckoutStore,
    onBack: () -> Unit,
    onPaymentComplete: () -> Unit,
) {
    val state by store.state.collectAsState()
    var step by remember { mutableStateOf(0) }
    val totalSteps = listOf("Review", "Payment", "Tender")

    LaunchedEffect(store) {
        store.effects.collect { eff ->
            when (eff) {
                CheckoutEffect.PaymentSucceeded -> onPaymentComplete()
                else -> Unit
            }
        }
    }

    Scaffold(
        containerColor = Neutral5,
        topBar = {
            TopAppBar(
                title = { Text("Checkout", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (step > 0) step-- else onBack()
                        },
                        enabled = !state.isProcessing
                    ) {
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
            CheckoutStepProgress(currentStep = step, steps = totalSteps)
            HorizontalDivider(color = Neutral40, thickness = 0.5.dp)

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    val direction = if (targetState > initialState) 1 else -1
                    (slideInHorizontally(animationSpec = tween(250)) { it * direction } + fadeIn()) togetherWith
                        (slideOutHorizontally(animationSpec = tween(250)) { -it * direction } + fadeOut())
                },
                label = "checkout-step",
                modifier = Modifier.weight(1f)
            ) { currentStep ->
                when (currentStep) {
                    0 -> ReviewStep(store, onNext = { step = 1 })
                    1 -> PaymentMethodStep(store, onNext = { step = 2 })
                    2 -> TenderStep(store)
                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun ReviewStep(store: CheckoutStore, onNext: () -> Unit) {
    val state by store.state.collectAsState()
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(SahmSpacing.xl)
    ) {
        Text("Order Summary", style = MaterialTheme.typography.titleLarge, color = Neutral95)
        Spacer(Modifier.height(SahmSpacing.md))
        Surface(
            shape = RoundedCornerShape(SahmRadius.md),
            color = Elevation1,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(SahmSpacing.lg)) {
                state.items.forEachIndexed { idx, ci ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${ci.product.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Neutral95,
                            modifier = Modifier.weight(1f))
                        Text("x${ci.quantity}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Neutral60,
                            modifier = Modifier.padding(end = SahmSpacing.sm))
                        Text(ci.lineTotal.toDisplayString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = Neutral80)
                    }
                    if (idx < state.items.lastIndex) {
                        HorizontalDivider(color = Neutral40, thickness = 0.5.dp)
                    }
                }
                Spacer(Modifier.height(SahmSpacing.md))
                HorizontalDivider(color = Neutral40, thickness = 0.5.dp)
                Spacer(Modifier.height(SahmSpacing.md))
                SummaryRow("Subtotal", state.totals.subtotal.toDisplayString())
                SummaryRow("Tax (14%)", state.totals.taxAmount.toDisplayString())
                Spacer(Modifier.height(SahmSpacing.sm))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("TOTAL", style = MaterialTheme.typography.titleMedium, color = Neutral95)
                    Text(state.totals.grandTotal.toDisplayString(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = BrandPrimary)
                }
            }
        }
        Spacer(Modifier.height(SahmSpacing.xl))
        PrimaryGradientButton(
            text = "Continue to Payment",
            onClick = onNext
        )
        Spacer(Modifier.height(SahmSpacing.xl))
    }
}

@Composable
private fun PaymentMethodStep(store: CheckoutStore, onNext: () -> Unit) {
    val state by store.state.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(SahmSpacing.xl)) {
        Text("Select Payment Method", style = MaterialTheme.typography.titleLarge, color = Neutral95)
        Spacer(Modifier.height(SahmSpacing.lg))
        PaymentMethodCard(
            icon = Icons.Rounded.AttachMoney,
            name = "Cash",
            description = "Pay with physical cash at counter",
            selected = state.paymentMethod == PaymentMethod.CASH,
            onClick = { store.dispatch(CheckoutIntent.SetPaymentMethod(PaymentMethod.CASH)) }
        )
        Spacer(Modifier.height(SahmSpacing.md))
        PaymentMethodCard(
            icon = Icons.Rounded.CreditCard,
            name = "Card",
            description = "Tap, chip, or swipe payment",
            selected = state.paymentMethod == PaymentMethod.CARD,
            onClick = { store.dispatch(CheckoutIntent.SetPaymentMethod(PaymentMethod.CARD)) }
        )
        Spacer(Modifier.weight(1f))
        PrimaryGradientButton(
            text = "Continue",
            onClick = onNext
        )
        Spacer(Modifier.height(SahmSpacing.xl))
    }
}

@Composable
private fun PaymentMethodCard(
    icon: ImageVector,
    name: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(SahmRadius.md),
        color = if (selected) BrandPrimaryContainer else Elevation1,
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(2.dp, BrandPrimary)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Neutral40)
        }
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(SahmSpacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = BrandPrimary,
                modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(SahmSpacing.lg))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleMedium, color = Neutral95)
                Text(description, style = MaterialTheme.typography.bodySmall, color = Neutral60)
            }
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = BrandPrimary,
                    unselectedColor = Neutral60
                )
            )
        }
    }
}

@Composable
private fun TenderStep(store: CheckoutStore) {
    val state by store.state.collectAsState()
    when (state.paymentMethod) {
        PaymentMethod.CASH -> CashTender(store)
        PaymentMethod.CARD -> CardTender(store)
    }
}

@Composable
private fun CashTender(store: CheckoutStore) {
    val state by store.state.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(SahmSpacing.xl)) {
        // Quick amounts
        val total = state.totals.grandTotal.amount
        val quickAmounts = remember(total) {
            listOf(
                "Exact" to total,
                "+50" to 5000L,
                "+100" to 10000L,
                "+200" to 20000L
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(SahmSpacing.sm),
            modifier = Modifier.fillMaxWidth()) {
            quickAmounts.forEach { (label, amount) ->
                QuickAmountChip(
                    label = label,
                    onClick = {
                        val newTendered = if (label == "Exact") {
                            amount
                        } else {
                            state.tendered.amount + amount
                        }
                        store.dispatch(CheckoutIntent.SetTendered(
                            Money(newTendered, state.tendered.currency)
                        ))
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(Modifier.height(SahmSpacing.lg))
        // Tendered display
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(SahmRadius.md),
            color = Elevation2
        ) {
            Column(modifier = Modifier.padding(SahmSpacing.lg)) {
                Text("Cash Tendered", style = MaterialTheme.typography.labelLarge, color = Neutral60)
                Spacer(Modifier.height(SahmSpacing.xs))
                Text(state.tendered.toDisplayString(),
                    style = MaterialTheme.typography.displaySmall,
                    color = BrandPrimary)
            }
        }
        Spacer(Modifier.height(SahmSpacing.md))
        // Change preview
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Elevation1, RoundedCornerShape(SahmRadius.sm))
                .padding(horizontal = SahmSpacing.lg, vertical = SahmSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Change", style = MaterialTheme.typography.bodyLarge, color = Neutral60)
            Text(
                state.change.toDisplayString(),
                style = MaterialTheme.typography.titleLarge,
                color = if (state.tendered.amount >= state.totals.grandTotal.amount) AccentTeal else SahmError
            )
        }
        Spacer(Modifier.height(SahmSpacing.lg))
        // Numeric keypad
        Keypad(
            onDigit = { d ->
                val newAmount = state.tendered.amount * 10 + d
                store.dispatch(CheckoutIntent.SetTendered(Money(newAmount, state.tendered.currency)))
            },
            onBackspace = {
                val newAmount = state.tendered.amount / 10
                store.dispatch(CheckoutIntent.SetTendered(Money(newAmount, state.tendered.currency)))
            }
        )
        Spacer(Modifier.height(SahmSpacing.lg))
        PrimaryGradientButton(
            text = "Confirm Cash Payment",
            onClick = { store.dispatch(CheckoutIntent.ConfirmPayment) },
            enabled = state.canConfirm && !state.isProcessing
        )
        Spacer(Modifier.height(SahmSpacing.xl))
    }
}

@Composable
private fun CardTender(store: CheckoutStore) {
    val state by store.state.collectAsState()
    val infinite = rememberInfiniteTransition(label = "card-pulse")
    val scale by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "scale"
    )
    val alpha by infinite.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "alpha"
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(SahmSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(0.4f))
        Box(modifier = Modifier.size(180.dp), contentAlignment = Alignment.Center) {
            // Pulse ring
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
                    .background(BrandPrimary, CircleShape)
            )
            // Inner card icon
            Surface(
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                color = BrandPrimaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Rounded.Contactless,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(SahmSpacing.xl))
        Text("Tap Card on Terminal",
            style = MaterialTheme.typography.headlineSmall, color = Neutral95)
        Spacer(Modifier.height(SahmSpacing.sm))
        Text("Hold the card or device near the payment terminal.",
            style = MaterialTheme.typography.bodyLarge,
            color = Neutral60,
            textAlign = TextAlign.Center)
        Spacer(Modifier.height(SahmSpacing.xl))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Nfc, contentDescription = null, tint = AccentTeal,
                modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(SahmSpacing.sm))
            Text("Terminal ready", style = MaterialTheme.typography.labelLarge, color = AccentTeal)
        }
        Spacer(Modifier.weight(1f))
        PrimaryGradientButton(
            text = "Confirm Card Payment",
            onClick = { store.dispatch(CheckoutIntent.ConfirmPayment) },
            enabled = !state.isProcessing
        )
        Spacer(Modifier.height(SahmSpacing.xl))
    }
}

@Composable
private fun QuickAmountChip(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = Neutral20
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = Neutral80)
        }
    }
}

@Composable
private fun Keypad(onDigit: (Int) -> Unit, onBackspace: () -> Unit) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("00", "0", "DEL")
    )
    Column(verticalArrangement = Arrangement.spacedBy(SahmSpacing.sm)) {
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SahmSpacing.sm)) {
                row.forEach { key ->
                    KeypadKey(
                        label = key,
                        onClick = {
                            when (key) {
                                "DEL" -> onBackspace()
                                "00" -> { onDigit(0); onDigit(0) }
                                else -> onDigit(key.toInt())
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadKey(label: String, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        modifier = modifier
            .height(60.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(SahmRadius.sm),
        color = Neutral10,
        border = androidx.compose.foundation.BorderStroke(1.dp, Neutral20)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (label == "DEL") {
                Icon(Icons.Rounded.Backspace, contentDescription = "Backspace",
                    tint = Neutral60, modifier = Modifier.size(20.dp))
            } else {
                Text(label, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = Neutral95)
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Neutral60)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = Neutral80)
    }
}
