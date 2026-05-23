package com.sahmfood.pos.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.ShoppingCart
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
 * Plaza-style 5-tab bottom navigation. Active item gets an orange pill
 * highlight (primary @ 15%) with rounded filled icon + brand-color label.
 * Inactive items are icon-only on `secondaryTextColor`.
 *
 * Cart tab shows a count badge anchored to its icon.
 */
data class BottomTab(
    val key: String,
    val iconActive: ImageVector,
    val iconIdle: ImageVector,
)

val PlazaBottomTabs = listOf(
    BottomTab("home", Icons.Rounded.Home, Icons.Outlined.Home),
    BottomTab("cart", Icons.Rounded.ShoppingCart, Icons.Outlined.ShoppingCart),
    BottomTab("menu", Icons.Rounded.Category, Icons.Outlined.Category),
    BottomTab("orders", Icons.Rounded.ShoppingBag, Icons.Outlined.ShoppingBag),
    BottomTab("profile", Icons.Rounded.Person, Icons.Outlined.Person),
)

/** Resolves a tab's display label from the currently-active strings. */
fun labelFor(key: String, str: com.sahmfood.pos.ui.strings.SahmStrings): String = when (key) {
    "home" -> str.navHome
    "cart" -> str.navCart
    "menu" -> str.navCategories
    "orders" -> str.navOrders
    "profile" -> str.navProfile
    else -> key
}

@Composable
fun PlazaBottomNav(
    selectedKey: String,
    onSelect: (String) -> Unit,
    cartCount: Int = 0,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.10f),
            )
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlazaBottomTabs.forEach { tab ->
            BottomNavTile(
                tab = tab,
                selected = selectedKey == tab.key,
                badge = if (tab.key == "cart") cartCount else 0,
                onClick = { onSelect(tab.key) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun BottomNavTile(
    tab: BottomTab,
    selected: Boolean,
    badge: Int,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    val label = labelFor(tab.key, strings)
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
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(bg, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = if (selected) tab.iconActive else tab.iconIdle,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp),
            )
            if (badge > 0) {
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp, bottom = 12.dp)
                        .height(18.dp)
                        .background(BrandPrimary, CircleShape)
                        .padding(horizontal = if (badge > 9) 5.dp else 4.dp, vertical = 1.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (badge > 99) "99+" else badge.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            ),
            color = contentColor,
        )
    }
}
