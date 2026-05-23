package com.sahmfood.pos.ui.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.domain.entities.CartItem
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.Elevation1
import com.sahmfood.pos.ui.theme.Neutral20
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral80
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing
import com.sahmfood.pos.ui.theme.categoryGradient

@Composable
fun CartLineItem(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SahmRadius.md),
        colors = CardDefaults.cardColors(containerColor = Elevation1),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SahmSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini image circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        brush = Brush.linearGradient(categoryGradient(item.product.category)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon(item.product.category),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(SahmSpacing.md))
            // Name + unit + line total
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.product.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = Neutral95,
                    maxLines = 1
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    item.unitPrice.toDisplayString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral60
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    item.lineTotal.toDisplayString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = Neutral80
                )
            }
            // Inline stepper
            Row(verticalAlignment = Alignment.CenterVertically) {
                MiniStepper(icon = Icons.Rounded.Remove, primary = false, onClick = onDecrement)
                Text(
                    item.quantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = Neutral95,
                    modifier = Modifier.width(28.dp),
                    textAlign = TextAlign.Center
                )
                MiniStepper(icon = Icons.Rounded.Add, primary = true, onClick = onIncrement)
            }
        }
    }
}

@Composable
private fun MiniStepper(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    primary: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(if (primary) BrandPrimary else Neutral20, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (primary) Color.White else Neutral80,
            modifier = Modifier.size(16.dp)
        )
    }
}
