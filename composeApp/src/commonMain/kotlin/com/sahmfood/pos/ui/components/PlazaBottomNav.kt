package com.sahmfood.pos.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryContainer
import com.sahmfood.pos.ui.theme.Neutral80
import com.sahmfood.pos.ui.theme.pressScale

/**
 * Plaza-style 5-tab bottom navigation.
 *
 * Design choices:
 *  - A single sliding "selection indicator" pill (orange container) glides
 *    horizontally between cells via [animateDpAsState] with a critically-
 *    damped spring. This is what makes the nav feel premium — a per-tile
 *    background just snaps; a single sliding background flows.
 *  - The selected icon pops from 1.0x to 1.15x via [animateFloatAsState],
 *    swaps from outlined to filled, and shifts to the brand color.
 *  - Each tile has [Modifier.pressScale] press feedback (0.92x), so taps
 *    feel physical even before the page transition starts.
 *  - The cart-count badge keeps its existing position/anim.
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
    val selectedIndex = PlazaBottomTabs.indexOfFirst { it.key == selectedKey }
        .coerceAtLeast(0)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.14f),
            )
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        // Outer container has 8dp horizontal padding; tiles each take 1/N of
        // the remaining width.
        val sidePadding = 8.dp
        val cellWidth: Dp = (maxWidth - sidePadding * 2) / PlazaBottomTabs.size
        // The pill is slightly inset so it doesn't touch its neighbours.
        val pillInset = 6.dp
        val pillWidth = cellWidth - pillInset * 2
        val targetX = sidePadding + (cellWidth * selectedIndex) + pillInset

        // Sliding indicator pill. Spring keeps motion lively without overshoot.
        val animatedX by animateDpAsState(
            targetValue = targetX,
            animationSpec = spring(
                dampingRatio = 0.78f,
                stiffness = Spring.StiffnessMedium,
            ),
            label = "nav-pill-x",
        )
        Box(
            modifier = Modifier
                .offset(x = animatedX, y = 8.dp)
                .width(pillWidth)
                .height(52.dp)
                .background(BrandPrimaryContainer, RoundedCornerShape(14.dp)),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = sidePadding, vertical = 6.dp),
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
    val contentColor by animateColorAsState(
        targetValue = if (selected) BrandPrimary else Neutral80,
        animationSpec = tween(220),
        label = "nav-fg",
    )
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.18f else 1f,
        animationSpec = spring(
            dampingRatio = 0.45f,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "nav-icon-scale",
    )
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .pressScale(interaction, pressedScale = 0.92f)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = if (selected) tab.iconActive else tab.iconIdle,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer { scaleX = iconScale; scaleY = iconScale },
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
            maxLines = 1,
        )
    }
}
