package com.sahmfood.pos.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalDining
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryContainer
import com.sahmfood.pos.ui.theme.Neutral80

/**
 * Plaza-style 5-item bottom navigation. Active item gets a pill highlight
 * (primary @ 15% background, rounded filled icon, primary-colored label).
 */
data class BottomNavItem(
    val key: String,
    val label: String,
    val iconActive: ImageVector,
    val iconIdle: ImageVector,
    val badge: Int = 0,
)

val DefaultBottomNavItems = listOf(
    BottomNavItem("home", "Home", Icons.Rounded.Home, Icons.Outlined.Home),
    BottomNavItem("menu", "Menu", Icons.Rounded.LocalDining, Icons.Outlined.LocalDining),
    BottomNavItem("cart", "Cart", Icons.Rounded.ShoppingBag, Icons.Outlined.ShoppingBag),
    BottomNavItem("orders", "Orders", Icons.Rounded.Receipt, Icons.Outlined.Receipt),
    BottomNavItem("store", "Store", Icons.Rounded.Storefront, Icons.Outlined.Storefront),
)

@Composable
fun PlazaBottomNav(
    items: List<BottomNavItem>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.10f),
            )
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            BottomNavTile(
                item = item,
                selected = selectedKey == item.key,
                onClick = { onSelect(item.key) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun BottomNavTile(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val bg by animateColorAsState(
        targetValue = if (selected) BrandPrimaryContainer else Color.Transparent,
        animationSpec = tween(200),
        label = "nav-bg",
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) BrandPrimary else Neutral80,
        animationSpec = tween(200),
        label = "nav-fg",
    )
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(bg, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (selected) item.iconActive else item.iconIdle,
                contentDescription = item.label,
                tint = contentColor,
                modifier = Modifier.size(22.dp),
            )
            if (item.badge > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(start = 12.dp, bottom = 12.dp)
                        .size(16.dp)
                        .background(BrandPrimary, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        item.badge.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
            }
        }
        if (selected) {
            Spacer(Modifier.width(6.dp))
            Text(
                item.label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                ),
                color = contentColor,
            )
        }
    }
}
