package com.sahmfood.pos.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.sahmfood.pos.presentation.checkout.CheckoutIntent
import com.sahmfood.pos.presentation.checkout.CheckoutStore
import com.sahmfood.pos.ui.components.PlazaPrimaryButton
import com.sahmfood.pos.ui.components.ReceiptPreview
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryLight
import com.sahmfood.pos.ui.theme.Neutral5
import com.sahmfood.pos.ui.theme.Neutral80
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing
import com.sahmfood.pos.ui.theme.SahmSuccess
import kotlinx.coroutines.delay

@Composable
fun ReceiptScreen(
    store: CheckoutStore,
    onNewOrder: () -> Unit,
    onTrackOrder: () -> Unit = {},
) {
    val state by store.state.collectAsState()
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    val scroll = rememberScrollState()
    var animationStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        animationStarted = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral5)
            .verticalScroll(scroll),
    ) {
        // Hero gradient success area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(BrandPrimary, BrandPrimaryLight),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val circleScale by animateFloatAsState(
                    targetValue = if (animationStarted) 1f else 0f,
                    animationSpec = spring(
                        dampingRatio = 0.5f,
                        stiffness = Spring.StiffnessMedium,
                    ),
                    label = "success-circle",
                )
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .graphicsLayer { scaleX = circleScale; scaleY = circleScale }
                        .shadow(elevation = 12.dp, shape = CircleShape)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = null,
                        tint = SahmSuccess,
                        modifier = Modifier.size(56.dp),
                    )
                }
                Spacer(Modifier.height(SahmSpacing.xl))
                AnimatedVisibility(
                    visible = animationStarted,
                    enter = slideInVertically(tween(300)) { it / 2 } + fadeIn(),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            strings.receiptPaymentSuccessful,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                            ),
                            color = Color.White,
                        )
                        Spacer(Modifier.height(SahmSpacing.xs))
                        state.completedOrder?.let { order ->
                            Text(
                                strings.receiptOrderNumberPrefix + order.id.takeLast(6).uppercase(),
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                                color = Color.White.copy(alpha = 0.92f),
                            )
                        }
                    }
                }
            }
        }

        // Receipt card
        AnimatedVisibility(
            visible = animationStarted && state.printedReceiptText != null,
            enter = fadeIn(tween(durationMillis = 400, delayMillis = 500))
                + slideInVertically(tween(400, 500)) { it / 4 },
            modifier = Modifier
                .fillMaxWidth()
                .padding(SahmSpacing.lg),
        ) {
            val text = state.printedReceiptText ?: ""
            Surface(
                shape = RoundedCornerShape(SahmRadius.lg),
                color = Color.White,
                shadowElevation = 4.dp,
            ) {
                ReceiptPreview(text = text)
            }
        }

        // Action row
        AnimatedVisibility(
            visible = animationStarted,
            enter = fadeIn(tween(durationMillis = 400, delayMillis = 800)),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = SahmSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(SahmSpacing.sm),
            ) {
                PlazaPrimaryButton(
                    text = strings.receiptTrackOrder,
                    onClick = onTrackOrder,
                    leadingIcon = Icons.Rounded.Visibility,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(SahmSpacing.sm),
                ) {
                    OutlinedAction(
                        text = strings.receiptReprint,
                        icon = Icons.Rounded.Print,
                        onClick = { store.dispatch(CheckoutIntent.PrintReceipt) },
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedAction(
                        text = strings.receiptNewOrder,
                        icon = Icons.Rounded.Add,
                        onClick = onNewOrder,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        Spacer(Modifier.height(SahmSpacing.xxl))
    }
}

@Composable
private fun OutlinedAction(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(SahmRadius.md),
        color = Color.White,
        border = BorderStroke(1.5.dp, BrandPrimary),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = BrandPrimary,
                    modifier = Modifier.size(20.dp))
                Spacer(Modifier.size(SahmSpacing.sm))
                Text(
                    text,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                    ),
                    color = BrandPrimary,
                )
            }
        }
    }
}
