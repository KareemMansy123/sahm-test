package com.sahmfood.pos.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.Neutral20
import com.sahmfood.pos.ui.theme.Neutral80
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmSpacing
import com.sahmfood.pos.ui.theme.categoryPastel

/**
 * Plaza-style category strip — pastel-cycled circular icons with two-line
 * label below. Selected state lifts the icon and darkens to brand color.
 */
@Composable
fun CategoryStrip(
    categories: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
    labelFor: (String?) -> String = { it ?: "All" },
) {
    val all: List<String?> = listOf(null) + categories
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = SahmSpacing.lg, vertical = SahmSpacing.sm),
        horizontalArrangement = Arrangement.spacedBy(SahmSpacing.md),
    ) {
        itemsIndexed(all) { index, category ->
            CategoryItem(
                label = labelFor(category),
                icon = category,
                color = categoryPastel(index),
                selected = selected == category,
                onClick = { onSelect(category) },
            )
        }
    }
}

@Composable
private fun CategoryItem(
    label: String,
    icon: String?,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.06f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium),
        label = "cat-scale",
    )
    val circleBg by animateColorAsState(
        targetValue = if (selected) BrandPrimary else color,
        animationSpec = tween(200),
        label = "cat-bg",
    )
    val iconTint by animateColorAsState(
        targetValue = if (selected) Color.White else Neutral80,
        animationSpec = tween(200),
        label = "cat-tint",
    )
    Column(
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .background(circleBg, CircleShape)
                .then(
                    if (selected) Modifier
                    else Modifier.border(1.dp, Neutral20, CircleShape),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = categoryIcon(icon),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.height(SahmSpacing.sm))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 14.sp,
            ),
            color = if (selected) Neutral95 else Neutral80,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}
