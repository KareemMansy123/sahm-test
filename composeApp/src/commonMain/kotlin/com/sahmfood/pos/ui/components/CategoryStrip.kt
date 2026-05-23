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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.Elevation1
import com.sahmfood.pos.ui.theme.Neutral40
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.SahmDimens
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing
import androidx.compose.ui.graphics.Color

@Composable
fun CategoryStrip(
    categories: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val all: List<String?> = listOf(null) + categories
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = SahmSpacing.xl, vertical = SahmSpacing.sm),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(all) { category ->
            CategoryCard(
                label = category ?: "All",
                icon = category,
                selected = selected == category,
                onClick = { onSelect(category) }
            )
        }
    }
}

@Composable
private fun CategoryCard(
    label: String,
    icon: String?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium),
        label = "category-scale"
    )
    val bg by animateColorAsState(
        targetValue = if (selected) BrandPrimary else Elevation1,
        animationSpec = tween(150),
        label = "category-bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) Color.White else Neutral60,
        animationSpec = tween(150),
        label = "category-fg"
    )

    Box(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .then(
                if (selected) Modifier.shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(SahmRadius.md),
                    ambientColor = BrandPrimary,
                    spotColor = BrandPrimary
                )
                else Modifier
            )
            .width(SahmDimens.categoryCardWidth)
            .height(SahmDimens.categoryCardHeight)
            .background(bg, RoundedCornerShape(SahmRadius.md))
            .then(
                if (selected) Modifier
                else Modifier.border(1.dp, Neutral40, RoundedCornerShape(SahmRadius.md))
            )
            .clickable(onClick = onClick)
            .padding(SahmSpacing.sm)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = categoryIcon(icon),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.size(SahmSpacing.xs))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                maxLines = 2,
                textAlign = TextAlign.Center
            )
        }
    }
}
