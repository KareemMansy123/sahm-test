package com.sahmfood.pos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.PaymentMethod
import com.sahmfood.pos.presentation.checkout.CheckoutEffect
import com.sahmfood.pos.presentation.checkout.CheckoutIntent
import com.sahmfood.pos.presentation.checkout.CheckoutStore
import com.sahmfood.pos.ui.components.NumericKeypad
import com.sahmfood.pos.ui.theme.SahmDimens
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    store: CheckoutStore,
    onBack: () -> Unit,
    onPaymentComplete: () -> Unit
) {
    val state by store.state.collectAsState()

    LaunchedEffect(store) {
        store.effects.collect { effect ->
            when (effect) {
                CheckoutEffect.PaymentSucceeded -> onPaymentComplete()
                CheckoutEffect.NavigateBack -> onBack()
                else -> Unit
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !state.isProcessing) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(SahmSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(SahmSpacing.lg)
        ) {
            // Order summary
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(SahmRadius.lg)
            ) {
                Column(modifier = Modifier.padding(SahmSpacing.lg)) {
                    Text("Order Summary", style = MaterialTheme.typography.titleMedium)
                    state.items.forEach { ci ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${ci.product.name} x${ci.quantity}",
                                style = MaterialTheme.typography.bodyMedium)
                            Text(ci.lineTotal.toDisplayString(),
                                style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(vertical = SahmSpacing.sm)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TOTAL", style = MaterialTheme.typography.titleMedium)
                        Text(
                            state.totals.grandTotal.toDisplayString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Payment method
            Row(horizontalArrangement = Arrangement.spacedBy(SahmSpacing.sm)) {
                FilterChip(
                    selected = state.paymentMethod == PaymentMethod.CASH,
                    onClick = { store.dispatch(CheckoutIntent.SetPaymentMethod(PaymentMethod.CASH)) },
                    label = { Text("Cash") }
                )
                FilterChip(
                    selected = state.paymentMethod == PaymentMethod.CARD,
                    onClick = { store.dispatch(CheckoutIntent.SetPaymentMethod(PaymentMethod.CARD)) },
                    label = { Text("Card") }
                )
            }

            if (state.paymentMethod == PaymentMethod.CASH) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(SahmRadius.lg)
                ) {
                    Column(modifier = Modifier.padding(SahmSpacing.lg)) {
                        Text(
                            "Cash Tendered",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            state.tendered.toDisplayString(),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (state.tendered.amount >= state.totals.grandTotal.amount && state.totals.grandTotal.amount > 0) {
                            Text(
                                "Change: ${state.change.toDisplayString()}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.padding(top = SahmSpacing.sm)
                            )
                        }
                    }
                }
                NumericKeypad(
                    onDigit = { d ->
                        val newAmount = state.tendered.amount * 10 + d
                        store.dispatch(CheckoutIntent.SetTendered(Money(newAmount, state.tendered.currency)))
                    },
                    onDecimal = { /* tendered is whole piastres; decimal is a no-op for the demo */ },
                    onBackspace = {
                        val newAmount = state.tendered.amount / 10
                        store.dispatch(CheckoutIntent.SetTendered(Money(newAmount, state.tendered.currency)))
                    }
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { store.dispatch(CheckoutIntent.ConfirmPayment) },
                    enabled = state.canConfirm && !state.isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SahmDimens.primaryButtonHeight),
                    shape = RoundedCornerShape(SahmRadius.md),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (state.isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = SahmSpacing.sm),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text("Confirm Payment", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
