package com.sahmfood.pos.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryLight
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing
import com.sahmfood.pos.ui.theme.pressScaleAuto

/**
 * Plaza's signature hero banner. Orange gradient surface with decorative
 * white circles, large headline, subtitle, and a white pill CTA.
 *
 * For the POS we use it to surface a daily-sales callout or a promotional
 * combo. Tap calls back via [onCtaClick].
 */
@Composable
fun PlazaHomeBanner(
    title: String,
    subtitle: String,
    ctaLabel: String,
    onCtaClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = SahmSpacing.lg)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(SahmRadius.lg),
                ambientColor = BrandPrimary.copy(alpha = 0.20f),
                spotColor = BrandPrimary.copy(alpha = 0.35f),
            )
            .clip(RoundedCornerShape(SahmRadius.lg))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(BrandPrimary, BrandPrimaryLight),
                ),
            ),
    ) {
        // Decorative circles (Plaza signature). Use offset (signed) instead of
        // padding (which throws on negative values). The parent Box clips to
        // the banner shape, so the offset circles peek visibly from each
        // corner without bleeding outside the card silhouette.
        //
        // Each circle slowly drifts in scale to give the banner subtle life.
        val drift = rememberInfiniteTransition(label = "banner-drift")
        val bigScale by drift.animateFloat(
            initialValue = 1f,
            targetValue = 1.12f,
            animationSpec = infiniteRepeatable(tween(4200), RepeatMode.Reverse),
            label = "big-scale",
        )
        val smallScale by drift.animateFloat(
            initialValue = 1.1f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(tween(3600), RepeatMode.Reverse),
            label = "small-scale",
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-30).dp)
                .size(120.dp)
                .graphicsLayer { scaleX = bigScale; scaleY = bigScale }
                .background(Color.White.copy(alpha = 0.10f), CircleShape),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-40).dp, y = 20.dp)
                .size(70.dp)
                .graphicsLayer { scaleX = smallScale; scaleY = smallScale }
                .background(Color.White.copy(alpha = 0.12f), CircleShape),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SahmSpacing.xl),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp,
                    ),
                    color = Color.White,
                )
                Spacer(Modifier.height(SahmSpacing.xs))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                )
            }
            Row(
                modifier = Modifier
                    .pressScaleAuto(pressedScale = 0.93f)
                    .background(Color.White, RoundedCornerShape(SahmRadius.xl))
                    .clickable(onClick = onCtaClick)
                    .padding(horizontal = SahmSpacing.lg, vertical = SahmSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    ctaLabel,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = BrandPrimary,
                )
            }
        }
    }
}

