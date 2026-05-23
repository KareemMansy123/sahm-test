package com.sahmfood.pos.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryDark
import com.sahmfood.pos.ui.theme.BrandPrimaryLight
import com.sahmfood.pos.ui.theme.SahmDimens
import com.sahmfood.pos.ui.theme.SahmSpacing
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun HeroHeader(
    modifier: Modifier = Modifier,
    isTablet: Boolean = false,
    cashierName: String = "Cashier",
) {
    val now = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
    val time = remember(now) {
        val h = now.hour.toString().padStart(2, '0')
        val m = now.minute.toString().padStart(2, '0')
        "$h:$m"
    }
    val dateLabel = remember(now) {
        val months = listOf("Jan","Feb","Mar","Apr","May","Jun",
            "Jul","Aug","Sep","Oct","Nov","Dec")
        "${months[now.monthNumber - 1]} ${now.dayOfMonth}, ${now.year}"
    }

    val height = if (isTablet) SahmDimens.heroHeaderTablet else SahmDimens.heroHeaderPhone

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(initialOffsetY = { -it / 2 }, animationSpec = tween(350))
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(BrandPrimary, BrandPrimaryDark),
                        start = Offset(0f, 0f),
                        end = Offset(1500f, 1500f)
                    )
                )
        ) {
            // Soft radial highlight
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                BrandPrimaryLight.copy(alpha = 0.30f),
                                Color.Transparent
                            ),
                            radius = 600f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = SahmSpacing.xl, end = SahmSpacing.xl, top = SahmSpacing.xl, bottom = SahmSpacing.xxl)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.Storefront,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.size(SahmSpacing.md))
                        Column {
                            Text(
                                "Sahm Food POS",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Counter · $cashierName",
                                color = Color.White.copy(alpha = 0.75f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            time,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            dateLabel,
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                Text(
                    "What's selling today?",
                    color = Color.White.copy(alpha = 0.92f),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
