package com.sahmfood.pos.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.domain.entities.OrderTotals
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryDark
import com.sahmfood.pos.ui.theme.BrandPrimaryLight
import com.sahmfood.pos.ui.theme.Elevation3
import com.sahmfood.pos.ui.theme.Neutral40
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral80
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmDurations
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing

@Composable
fun OrderTotalCard(
    totals: OrderTotals,
    onCharge: () -> Unit,
    chargeEnabled: Boolean,
    modifier: Modifier = Modifier,
    chargeLabel: String = "Proceed to Checkout",
) {
    val animatedTotal by animateFloatAsState(
        targetValue = totals.grandTotal.amount / 100f,
        animationSpec = tween(durationMillis = SahmDurations.long),
        label = "grand-total"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SahmRadius.lg),
        colors = CardDefaults.cardColors(containerColor = Elevation3),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(SahmSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(SahmSpacing.sm)
        ) {
            LabelRow("Subtotal", totals.subtotal.toDisplayString())
            LabelRow("Tax (14%)", totals.taxAmount.toDisplayString())
            if (totals.discount.amount > 0) {
                LabelRow("Discount", "- " + totals.discount.toDisplayString(),
                    color = MaterialTheme.colorScheme.tertiary)
            }
            HorizontalDivider(color = Neutral40, thickness = 0.5.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", style = MaterialTheme.typography.titleLarge, color = Neutral95)
                Text(
                    text = formatEgp(animatedTotal),
                    style = MaterialTheme.typography.headlineMedium,
                    color = BrandPrimary
                )
            }
            Spacer(Modifier.height(SahmSpacing.sm))
            PrimaryGradientButton(
                text = chargeLabel,
                onClick = onCharge,
                enabled = chargeEnabled,
                trailingIcon = Icons.Rounded.ChevronRight
            )
        }
    }
}

@Composable
fun PrimaryGradientButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    modifier: Modifier = Modifier,
) {
    val brush = if (enabled) {
        Brush.horizontalGradient(listOf(BrandPrimaryLight, BrandPrimaryDark))
    } else {
        Brush.horizontalGradient(listOf(Neutral40, Neutral40))
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(brush, RoundedCornerShape(SahmRadius.md))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, tint = Color.White,
                    modifier = Modifier.padding(end = SahmSpacing.sm))
            }
            Text(text, style = MaterialTheme.typography.titleMedium, color = Color.White)
            if (trailingIcon != null) {
                Icon(trailingIcon, contentDescription = null, tint = Color.White,
                    modifier = Modifier.padding(start = SahmSpacing.sm))
            }
        }
    }
}

@Composable
private fun LabelRow(label: String, value: String, color: Color = Neutral80) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = Neutral60)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = color)
    }
}

private fun formatEgp(value: Float): String {
    val whole = value.toInt()
    val cents = ((value - whole) * 100).toInt()
    val absCents = if (cents < 0) -cents else cents
    return "EGP $whole.${absCents.toString().padStart(2, '0')}"
}
