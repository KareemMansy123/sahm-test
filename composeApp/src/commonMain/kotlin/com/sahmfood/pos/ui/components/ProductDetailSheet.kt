package com.sahmfood.pos.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryDark
import com.sahmfood.pos.ui.theme.BrandPrimaryLight
import com.sahmfood.pos.ui.theme.Neutral20
import com.sahmfood.pos.ui.theme.Neutral40
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral80
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing
import com.sahmfood.pos.ui.theme.categoryGradient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailSheet(
    product: Product,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAdd: (quantity: Int) -> Unit,
) {
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    val lang = com.sahmfood.pos.ui.strings.currentLanguageCode()
    var quantity by remember(product.id) { mutableStateOf(1) }
    val total = remember(product, quantity) {
        Money(product.price.amount * quantity, product.price.currency)
    }
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = SahmRadius.xxl, topEnd = SahmRadius.xxl),
        containerColor = Color.White,
        dragHandle = {
            Box(modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)) {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Neutral40)
                )
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Hero image area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Brush.linearGradient(categoryGradient(product.category)))
            ) {
                Icon(
                    imageVector = categoryIcon(product.category),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.Center).size(72.dp)
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(SahmRadius.xs),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(SahmSpacing.lg)
                ) {
                    Text(
                        product.localizedCategory(lang),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SahmSpacing.xl)
            ) {
                Text(product.localizedName(lang), style = MaterialTheme.typography.headlineSmall, color = Neutral95)
                Spacer(Modifier.height(SahmSpacing.xs))
                Text(product.price.toDisplayString(),
                    style = MaterialTheme.typography.displaySmall,
                    color = BrandPrimary)
                Spacer(Modifier.height(SahmSpacing.md))
                Text(
                    product.localizedDescription(lang).ifBlank {
                        strings.productGenericDescriptionFallback(product.localizedCategory(lang))
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral60
                )

                // Quantity stepper — large, centered
                Spacer(Modifier.height(SahmSpacing.xl))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepperButton(
                        icon = Icons.Rounded.Remove,
                        enabled = quantity > 1,
                        onClick = { if (quantity > 1) quantity -= 1 },
                        primary = false
                    )
                    Spacer(Modifier.width(SahmSpacing.xl))
                    AnimatedContent(
                        targetState = quantity,
                        transitionSpec = {
                            val direction = if (targetState > initialState) 1 else -1
                            (slideInVertically { it * direction } + fadeIn()) togetherWith
                                (slideOutVertically { -it * direction } + fadeOut())
                        },
                        label = "qty-flip"
                    ) { q ->
                        Text(
                            text = q.toString(),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = Neutral95
                        )
                    }
                    Spacer(Modifier.width(SahmSpacing.xl))
                    StepperButton(
                        icon = Icons.Rounded.Add,
                        enabled = true,
                        onClick = { quantity += 1 },
                        primary = true
                    )
                }

                Spacer(Modifier.height(SahmSpacing.xl))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(strings.cartTotal, style = MaterialTheme.typography.titleMedium, color = Neutral60)
                    Text(
                        total.toDisplayString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = Neutral95
                    )
                }

                Spacer(Modifier.height(SahmSpacing.lg))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable {
                            onAdd(quantity)
                        },
                    shape = RoundedCornerShape(SahmRadius.md),
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(BrandPrimaryLight, BrandPrimaryDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Rounded.ShoppingCart,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(SahmSpacing.sm))
                            Text(
                                strings.productAddToOrderTemplate(quantity.toString()),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(Modifier.height(SahmSpacing.lg))
                Box(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}

@Composable
private fun StepperButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
    primary: Boolean
) {
    Surface(
        modifier = Modifier
            .size(56.dp)
            .clickable(enabled = enabled, onClick = onClick),
        shape = CircleShape,
        color = if (primary) BrandPrimary else Neutral20
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icon,
                contentDescription = null,
                tint = when {
                    primary -> Color.White
                    !enabled -> Neutral40
                    else -> Neutral80
                },
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
