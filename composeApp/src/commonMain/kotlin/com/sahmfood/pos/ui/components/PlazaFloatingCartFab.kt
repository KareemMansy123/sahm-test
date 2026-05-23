package com.sahmfood.pos.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryLight
import com.sahmfood.pos.ui.theme.SahmError

/**
 * Plaza's floating cart FAB — 60dp gradient circle with a red item-count
 * badge. Pulse-bounces when the count changes.
 */
@Composable
fun PlazaFloatingCartFab(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Pulse trigger flips on each count change → animateFloatAsState reads it
    // and animates the scale. The previous "if (count != lastCount)" pattern
    // would never fire because the LaunchedEffect that updates lastCount
    // runs AFTER composition, so the target was always 1f on the same frame.
    var pulseTrigger by remember { mutableIntStateOf(0) }
    LaunchedEffect(count) {
        if (count > 0) pulseTrigger++
    }
    val pulse by animateFloatAsState(
        targetValue = if (pulseTrigger % 2 == 1) 1.18f else 1f,
        animationSpec = spring(dampingRatio = 0.3f, stiffness = Spring.StiffnessHigh),
        label = "cart-pulse",
    )

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = pulse; scaleY = pulse }
            .size(60.dp)
            .shadow(
                elevation = 16.dp,
                shape = CircleShape,
                ambientColor = BrandPrimary.copy(alpha = 0.30f),
                spotColor = BrandPrimary.copy(alpha = 0.45f),
            )
            .background(
                brush = Brush.linearGradient(listOf(BrandPrimary, BrandPrimaryLight)),
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Rounded.ShoppingBag,
            contentDescription = "Open cart",
            tint = Color.White,
            modifier = Modifier.size(26.dp),
        )
        AnimatedVisibility(
            visible = count > 0,
            enter = scaleIn(spring(dampingRatio = 0.4f)) + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(SahmError, CircleShape)
                    .padding(2.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (count > 99) "99+" else count.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }
    }
}
