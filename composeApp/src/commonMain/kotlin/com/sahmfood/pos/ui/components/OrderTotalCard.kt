package com.sahmfood.pos.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.domain.entities.OrderTotals
import com.sahmfood.pos.ui.theme.SahmDimens
import com.sahmfood.pos.ui.theme.SahmDurations
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing

@Composable
fun OrderTotalCard(
    totals: OrderTotals,
    onCharge: () -> Unit,
    chargeEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    // Wow #1 — odometer roll-up on the grand total.
    val animatedTotal by animateFloatAsState(
        targetValue = totals.grandTotal.amount / 100f,
        animationSpec = tween(durationMillis = SahmDurations.long),
        label = "grand-total"
    )

    // Subtle scale bounce when totals change (proxy on grand total amount).
    val pulse by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = SahmDurations.medium),
        label = "pulse"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = pulse; scaleY = pulse },
        shape = RoundedCornerShape(SahmRadius.lg),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(SahmSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(SahmSpacing.sm)
        ) {
            LabelRow("Subtotal", totals.subtotal.toDisplayString(), emphasised = false)
            LabelRow("Tax (14%)", totals.taxAmount.toDisplayString(), emphasised = false)
            if (totals.discount.amount > 0) {
                LabelRow(
                    "Discount",
                    "- " + totals.discount.toDisplayString(),
                    emphasised = false,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "TOTAL",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatEgp(animatedTotal),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = onCharge,
                enabled = chargeEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SahmDimens.primaryButtonHeight),
                shape = RoundedCornerShape(SahmRadius.md),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Charge", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun LabelRow(
    label: String,
    value: String,
    emphasised: Boolean,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = color)
        Text(
            value,
            style = if (emphasised) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
            color = if (emphasised) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatEgp(value: Float): String {
    val whole = value.toInt()
    val cents = ((value - whole) * 100).toInt()
    val absCents = if (cents < 0) -cents else cents
    return "EGP $whole.${absCents.toString().padStart(2, '0')}"
}
