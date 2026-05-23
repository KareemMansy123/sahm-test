package com.sahmfood.pos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.domain.entities.CartItem
import com.sahmfood.pos.ui.theme.SahmDimens
import com.sahmfood.pos.ui.theme.SahmSpacing

@Composable
fun CartLineItem(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SahmSpacing.md, vertical = SahmSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SahmSpacing.sm)
    ) {
        // Stepper
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .padding(horizontal = SahmSpacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDecrement,
                modifier = Modifier.size(SahmDimens.minTouchTarget)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease quantity")
            }
            Text(
                text = item.quantity.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.width(28.dp),
                textAlign = TextAlign.Center
            )
            IconButton(
                onClick = onIncrement,
                modifier = Modifier.size(SahmDimens.minTouchTarget)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase quantity")
            }
        }

        // Name + unit price
        Column(modifier = Modifier.weight(1f)) {
            Text(item.product.name, style = MaterialTheme.typography.titleMedium, maxLines = 2)
            Text(
                item.unitPrice.toDisplayString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Line total
        Text(
            item.lineTotal.toDisplayString(),
            style = MaterialTheme.typography.titleMedium
        )

        IconButton(onClick = onRemove, modifier = Modifier.size(SahmDimens.minTouchTarget)) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove ${item.product.name}",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
