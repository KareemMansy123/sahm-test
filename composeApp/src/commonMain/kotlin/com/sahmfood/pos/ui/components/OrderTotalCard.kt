package com.sahmfood.pos.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.domain.entities.OrderTotals
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.FreeDeliveryColor
import com.sahmfood.pos.ui.theme.Neutral20
import com.sahmfood.pos.ui.theme.Neutral80
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmDurations
import com.sahmfood.pos.ui.theme.SahmSpacing

/**
 * Sahm order summary block. Used inside the pinned bottom summary
 * on the cart screen and as the "Order Summary" section on checkout.
 *
 * Lines: subtotal, tax (14%), discount (optional), total. Total flips with
 * an animated value.
 */
@Composable
fun OrderTotalCard(
    totals: OrderTotals,
    onCharge: () -> Unit,
    chargeEnabled: Boolean,
    modifier: Modifier = Modifier,
    chargeLabel: String = "Proceed to Checkout",
    showButton: Boolean = true,
) {
    // Animate the piastre value as a Float (Float can represent integer piastres
    // exactly up to ~16 million, which is well past any real receipt total),
    // then format with integer arithmetic to avoid sub-piastre drift.
    val animatedPiastres by animateFloatAsState(
        targetValue = totals.grandTotal.amount.toFloat(),
        animationSpec = tween(durationMillis = SahmDurations.long),
        label = "grand-total",
    )

    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SummaryRow(strings.cartSubtotal, totals.subtotal.toDisplayString())
        SummaryRow(strings.cartTax, totals.taxAmount.toDisplayString())
        if (totals.discount.amount > 0) {
            SummaryRow(
                strings.cartDiscount,
                "- " + totals.discount.toDisplayString(),
                color = FreeDeliveryColor,
            )
        }
        HorizontalDivider(color = Neutral20, thickness = 1.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                strings.cartTotal,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                ),
                color = Neutral95,
            )
            Text(
                text = formatEgp(animatedPiastres.toLong()),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                ),
                color = BrandPrimary,
            )
        }
        if (showButton) {
            Spacer(Modifier.height(SahmSpacing.sm))
            SahmPrimaryButton(
                text = chargeLabel,
                onClick = onCharge,
                enabled = chargeEnabled,
                trailingIcon = Icons.Rounded.ArrowForward,
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    color: Color = Neutral95,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = Neutral80,
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            color = color,
        )
    }
}

private fun formatEgp(piastres: Long): String {
    val whole = piastres / 100
    val cents = piastres % 100
    val absCents = if (cents < 0) -cents else cents
    return "EGP $whole.${absCents.toString().padStart(2, '0')}"
}

