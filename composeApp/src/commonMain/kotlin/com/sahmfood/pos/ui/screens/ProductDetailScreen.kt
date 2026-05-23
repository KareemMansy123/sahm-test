package com.sahmfood.pos.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.presentation.favorites.FavoritesIntent
import com.sahmfood.pos.presentation.favorites.FavoritesStore
import com.sahmfood.pos.ui.components.PlazaPrimaryButton
import com.sahmfood.pos.ui.components.categoryIcon
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.ExpressColor
import com.sahmfood.pos.ui.theme.Neutral10
import com.sahmfood.pos.ui.theme.Neutral40
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral80
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.PriceColor
import com.sahmfood.pos.ui.theme.RatingColor
import com.sahmfood.pos.ui.theme.SahmError
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing
import com.sahmfood.pos.ui.theme.categoryGradient

/**
 * Plaza-style product details screen. Full-screen image hero on top
 * with floating circular nav buttons over a fade gradient, then
 * stacked info cards (overview / description / specs), and a sticky
 * Add-to-Order bar at the bottom with an inline qty stepper.
 */
@Composable
fun ProductDetailScreen(
    product: Product,
    favoritesStore: FavoritesStore,
    onBack: () -> Unit,
    onAddToCart: (Int) -> Unit,
) {
    val favState by favoritesStore.state.collectAsState()
    val isFavorite = product.id in favState.favoriteIds
    var quantity by remember { mutableIntStateOf(1) }
    val totalPiastres = product.price.amount * quantity
    val total = Money(totalPiastres, product.price.currency)
    val scroll = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(bottom = 112.dp),  // clear bottom bar
        ) {
            // Hero image — fills width, square
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(
                        Brush.linearGradient(categoryGradient(product.category)),
                    ),
            ) {
                Icon(
                    categoryIcon(product.category),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.Center).size(120.dp),
                )
                // Bottom fade overlay so nav icons are readable
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.18f),
                                    Color.Transparent,
                                    Color.Transparent,
                                ),
                                startY = 0f,
                                endY = 300f,
                            ),
                        ),
                )
            }

            // Info section
            Column(modifier = Modifier.padding(SahmSpacing.xl)) {
                // Category + Express
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        product.category,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = Neutral60,
                    )
                    Spacer(Modifier.width(SahmSpacing.sm))
                    Row(
                        modifier = Modifier
                            .background(
                                ExpressColor.copy(alpha = 0.10f),
                                RoundedCornerShape(SahmRadius.xs),
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Rounded.Bolt,
                            contentDescription = null,
                            tint = ExpressColor,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.size(4.dp))
                        Text(
                            "Express",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                            ),
                            color = ExpressColor,
                        )
                    }
                }
                Spacer(Modifier.height(SahmSpacing.sm))
                Text(
                    product.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                    ),
                    color = Neutral95,
                )
                Spacer(Modifier.height(SahmSpacing.sm))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Star,
                        contentDescription = null,
                        tint = RatingColor,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.size(4.dp))
                    Text(
                        "4.5",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                        ),
                        color = Neutral95,
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(
                        "(120 reviews)",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = Neutral60,
                    )
                }
                Spacer(Modifier.height(SahmSpacing.lg))
                Text(
                    product.price.toDisplayString(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                    ),
                    color = PriceColor,
                )
                Spacer(Modifier.height(SahmSpacing.xl))

                // Description card
                InfoCard(title = "Description") {
                    Text(
                        product.description.ifBlank {
                            "Freshly prepared. Made to order. Our most-loved ${product.category.lowercase()} on the menu."
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                        ),
                        color = Neutral80,
                    )
                }

                Spacer(Modifier.height(SahmSpacing.lg))
                // Quick facts card
                InfoCard(title = "Quick Facts") {
                    SpecRow("Category", product.category)
                    Spacer(Modifier.height(6.dp))
                    SpecRow("Item code", product.id)
                    Spacer(Modifier.height(6.dp))
                    SpecRow("Availability", if (product.isAvailable) "In stock" else "Out of stock")
                    Spacer(Modifier.height(6.dp))
                    SpecRow("Prep time", "5–8 min")
                }
            }
        }

        // Top nav (back / share / favorite)
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(horizontal = SahmSpacing.lg, vertical = SahmSpacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIconButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBackIos,
                contentDescription = "Back",
                onClick = onBack,
            )
            Row {
                CircleIconButton(
                    icon = Icons.Rounded.IosShare,
                    contentDescription = "Share",
                    onClick = { /* no-op */ },
                )
                Spacer(Modifier.width(SahmSpacing.sm))
                CircleIconButton(
                    icon = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove favorite" else "Add favorite",
                    tint = if (isFavorite) SahmError else Neutral80,
                    onClick = { favoritesStore.dispatch(FavoritesIntent.Toggle(product.id)) },
                )
            }
        }

        // Sticky bottom Add-to-Cart bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color.White,
            shadowElevation = 12.dp,
            shape = RoundedCornerShape(topStart = SahmRadius.xxl, topEnd = SahmRadius.xxl),
        ) {
            Row(
                modifier = Modifier
                    .padding(SahmSpacing.lg)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Stepper
                Row(
                    modifier = Modifier
                        .background(Neutral10, RoundedCornerShape(SahmRadius.md))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StepperButton(
                        icon = Icons.Rounded.Remove,
                        enabled = quantity > 1,
                        onClick = { if (quantity > 1) quantity-- },
                    )
                    AnimatedContent(
                        targetState = quantity,
                        transitionSpec = {
                            val dir = if (targetState > initialState) 1 else -1
                            (slideInVertically { it * dir } + fadeIn()) togetherWith
                                (slideOutVertically { -it * dir } + fadeOut())
                        },
                        label = "qty",
                    ) { q ->
                        Text(
                            q.toString(),
                            modifier = Modifier
                                .width(36.dp)
                                .padding(horizontal = SahmSpacing.sm),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                            ),
                            color = Neutral95,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                    }
                    StepperButton(
                        icon = Icons.Rounded.Add,
                        enabled = true,
                        onClick = { quantity++ },
                        primary = true,
                    )
                }
                Spacer(Modifier.width(SahmSpacing.md))
                Box(modifier = Modifier.weight(1f)) {
                    PlazaPrimaryButton(
                        text = "Add ${total.toDisplayString()}",
                        leadingIcon = Icons.Rounded.ShoppingBag,
                        onClick = { onAddToCart(quantity) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CircleIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = Neutral80,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .shadow(elevation = 4.dp, shape = CircleShape)
            .background(Color.White, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun InfoCard(title: String, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SahmRadius.lg),
        color = Neutral10,
    ) {
        Column(modifier = Modifier.padding(SahmSpacing.lg)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                ),
                color = Neutral95,
            )
            Spacer(Modifier.height(SahmSpacing.sm))
            content()
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
            color = Neutral60)
        Text(value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
            color = Neutral95)
    }
}

@Composable
private fun StepperButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
    primary: Boolean = false,
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(
                color = if (primary) BrandPrimary else Color.White,
                shape = CircleShape,
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (primary) Color.White
                   else if (enabled) BrandPrimary else Neutral40,
            modifier = Modifier.size(18.dp),
        )
    }
}
