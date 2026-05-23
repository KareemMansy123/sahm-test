package com.sahmfood.pos.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryContainer
import com.sahmfood.pos.ui.theme.BrandPrimaryLight
import com.sahmfood.pos.ui.theme.Neutral40
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing

/**
 * Plaza empty state — 140dp tinted circle with brand-colored icon at 64dp,
 * bold title, description, and a primary CTA pill below.
 */
@Composable
fun PlazaEmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    ctaLabel: String? = null,
    onCta: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SahmSpacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(BrandPrimaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = BrandPrimary,
                modifier = Modifier.size(64.dp),
            )
        }
        Spacer(Modifier.height(SahmSpacing.xxl))
        Text(
            title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
            ),
            color = Neutral95,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(SahmSpacing.md))
        Text(
            description,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp,
                lineHeight = 22.sp,
            ),
            color = Neutral60,
            textAlign = TextAlign.Center,
        )
        if (ctaLabel != null && onCta != null) {
            Spacer(Modifier.height(SahmSpacing.xxl))
            PlazaPrimaryButton(text = ctaLabel, onClick = onCta)
        }
    }
}

/**
 * Plaza-style primary button — orange gradient, white text, 56dp height,
 * 16dp radius, brand-colored shadow. Canonical CTA across the app.
 */
@Composable
fun PlazaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    modifier: Modifier = Modifier,
) {
    val brush = if (enabled) {
        Brush.horizontalGradient(listOf(BrandPrimary, BrandPrimaryLight))
    } else {
        Brush.horizontalGradient(listOf(Neutral40, Neutral40))
    }
    val shadowModifier = if (enabled) {
        Modifier.shadow(
            elevation = 12.dp,
            shape = RoundedCornerShape(SahmRadius.md),
            ambientColor = BrandPrimary.copy(alpha = 0.30f),
            spotColor = BrandPrimary.copy(alpha = 0.40f),
        )
    } else Modifier
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .then(shadowModifier)
            .background(brush, RoundedCornerShape(SahmRadius.md))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(
                    leadingIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.size(SahmSpacing.sm))
            }
            Text(
                text,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                ),
                color = Color.White,
            )
            if (trailingIcon != null) {
                Spacer(Modifier.size(SahmSpacing.sm))
                Icon(
                    trailingIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
