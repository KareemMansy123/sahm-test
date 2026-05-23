package com.sahmfood.pos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingCartCheckout
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryContainer
import com.sahmfood.pos.ui.theme.Neutral40
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing

@Composable
fun EmptyCartState(
    onBrowse: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SahmSpacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(BrandPrimaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.ShoppingCartCheckout,
                contentDescription = null,
                tint = BrandPrimary,
                modifier = Modifier.size(56.dp)
            )
        }
        Spacer(Modifier.height(SahmSpacing.xl))
        Text(
            "Your order is empty",
            style = MaterialTheme.typography.headlineSmall,
            color = Neutral95,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(SahmSpacing.sm))
        Text(
            "Add items from the menu to start an order.",
            style = MaterialTheme.typography.bodyLarge,
            color = Neutral60,
            textAlign = TextAlign.Center
        )
        if (onBrowse != null) {
            Spacer(Modifier.height(SahmSpacing.xl))
            Box(
                modifier = Modifier
                    .clickable(onClick = onBrowse)
                    .padding(horizontal = SahmSpacing.xl, vertical = 12.dp)
                    .background(
                        color = androidx.compose.ui.graphics.Color.Transparent,
                        shape = RoundedCornerShape(SahmRadius.sm)
                    )
            ) {
                Text(
                    "Browse Menu",
                    style = MaterialTheme.typography.labelLarge,
                    color = BrandPrimary
                )
            }
        }
    }
}
