package com.sahmfood.pos.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
// `clip` is still used for the square product thumbnail (cart line item image).
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.domain.entities.CartItem
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.ExpressColor
import com.sahmfood.pos.ui.theme.Neutral10
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral80
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.PriceColor
import com.sahmfood.pos.ui.theme.SahmDimens
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing
import com.sahmfood.pos.ui.theme.pressScaleAuto
import com.sahmfood.pos.ui.theme.categoryGradient
import com.sahmfood.pos.ui.theme.sahmCardShadow

/**
 * Sahm cart item card — 90dp square thumbnail (12dp radius), express badge,
 * product name (14sp w600), inline qty controls + price.
 */
@Composable
fun CartLineItem(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .sahmCardShadow(shape = RoundedCornerShape(SahmRadius.lg), elevation = 2.dp),
        shape = RoundedCornerShape(SahmRadius.lg),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier.padding(SahmSpacing.md),
            verticalAlignment = Alignment.Top,
        ) {
            // Square product image
            Box(
                modifier = Modifier
                    .size(SahmDimens.cartItemImageSize)
                    .clip(RoundedCornerShape(SahmRadius.md))
                    .background(Brush.linearGradient(categoryGradient(item.product.category))),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    categoryIcon(item.product.category),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(36.dp),
                )
            }
            Spacer(Modifier.width(SahmSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                // Express badge
                Row(
                    modifier = Modifier
                        .background(
                            ExpressColor.copy(alpha = 0.10f),
                            RoundedCornerShape(SahmRadius.xs),
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Rounded.Bolt,
                        contentDescription = null,
                        tint = ExpressColor,
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(Modifier.size(2.dp))
                    Text(
                        "Express",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                        ),
                        color = ExpressColor,
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    item.product.localizedName(com.sahmfood.pos.ui.strings.currentLanguageCode()),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                    ),
                    color = Neutral95,
                    maxLines = 2,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    item.unitPrice.toDisplayString(),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Neutral60,
                )
                Spacer(Modifier.height(SahmSpacing.sm))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        item.lineTotal.toDisplayString(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                        ),
                        color = PriceColor,
                    )
                    Spacer(Modifier.weight(1f))
                    QuantityPill(
                        quantity = item.quantity,
                        onIncrement = onIncrement,
                        onDecrement = onDecrement,
                    )
                }
            }
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(28.dp),
            ) {
                Icon(
                    Icons.Rounded.DeleteOutline,
                    contentDescription = item.product.localizedName(com.sahmfood.pos.ui.strings.currentLanguageCode()),
                    tint = Neutral80,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun QuantityPill(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    Row(
        modifier = Modifier
            .height(36.dp)
            .background(Neutral10, RoundedCornerShape(SahmRadius.md))
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .pressScaleAuto()
                .background(Color.White, CircleShape)
                .clickable(onClick = onDecrement),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Rounded.Remove,
                contentDescription = null,
                tint = Neutral80,
                modifier = Modifier.size(16.dp),
            )
        }
        AnimatedContent(
            targetState = quantity,
            transitionSpec = {
                val direction = if (targetState > initialState) 1 else -1
                (slideInVertically { it * direction } + fadeIn()) togetherWith
                    (slideOutVertically { -it * direction } + fadeOut())
            },
            label = "qty-cart",
            modifier = Modifier
                .width(28.dp)
                .padding(horizontal = 2.dp),
        ) { q ->
            Text(
                q.toString(),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                ),
                color = Neutral95,
                textAlign = TextAlign.Center,
            )
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .pressScaleAuto()
                .background(BrandPrimary, CircleShape)
                .clickable(onClick = onIncrement),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Rounded.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
