package com.sahmfood.pos.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.ExpressColor
import com.sahmfood.pos.ui.theme.Neutral80
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.PriceColor
import com.sahmfood.pos.ui.theme.RatingColor
import com.sahmfood.pos.ui.theme.SahmError
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.categoryGradient
import com.sahmfood.pos.ui.theme.sahmCardShadow

/**
 * Sahm product card. White surface with subtle shadow, image hero
 * top with overlays (express badge, favorite, floating + add).
 *
 * - Card body tap → product detail
 * - Floating + button → direct add to cart
 * - Heart icon → toggle real favorite state
 */
@Composable
fun ProductCard(
    product: Product,
    isFavorite: Boolean,
    onCardTap: () -> Unit,
    onAdd: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    rating: Double = 4.5,
    isExpress: Boolean = true,
) {
    val lang = com.sahmfood.pos.ui.strings.currentLanguageCode()
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    val displayName = product.localizedName(lang)
    val displayCategory = product.localizedCategory(lang)
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
        label = "press-scale",
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .sahmCardShadow(shape = RoundedCornerShape(SahmRadius.md))
            .clickable(interactionSource = interaction, indication = null) { onCardTap() },
        shape = RoundedCornerShape(SahmRadius.md),
        color = Color.White,
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = SahmRadius.md, topEnd = SahmRadius.md))
                    .background(brush = Brush.linearGradient(categoryGradient(product.category))),
            ) {
                Icon(
                    imageVector = categoryIcon(product.category),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.Center).size(56.dp),
                )

                // Top-right favorite heart (signature)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .shadow(2.dp, CircleShape)
                        .background(Color.White, CircleShape)
                        .clickable(onClick = onToggleFavorite),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove favorite" else "Add favorite",
                        tint = if (isFavorite) SahmError else Neutral80,
                        modifier = Modifier.size(18.dp),
                    )
                }

                // Bottom-right floating + button
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(SahmRadius.sm),
                            ambientColor = BrandPrimary.copy(alpha = 0.25f),
                            spotColor = BrandPrimary.copy(alpha = 0.35f),
                        )
                        .background(BrandPrimary, RoundedCornerShape(SahmRadius.sm))
                        .clickable(onClick = onAdd),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = displayName,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (isExpress) {
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
                            strings.expressBadge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                            ),
                            color = ExpressColor,
                        )
                    }
                }
                Text(
                    displayName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        lineHeight = 17.sp,
                    ),
                    color = Neutral95,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Star,
                        contentDescription = null,
                        tint = RatingColor,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.size(2.dp))
                    Text(
                        ((rating * 10).toInt() / 10.0).toString(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                        ),
                        color = Neutral95,
                    )
                }
                Text(
                    product.price.toDisplayString(),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    ),
                    color = PriceColor,
                )
            }
        }
    }
}
