package com.sahmfood.pos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryContainer
import com.sahmfood.pos.ui.theme.Neutral80
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing

/**
 * the app's "Deliver to" header bar. We repurpose it for the POS as a
 * register / cashier identification strip: small location icon, a label
 * ("Cashier" or "Register"), then the cashier name, with a downward
 * chevron suggesting it could open a switcher.
 */
@Composable
fun SahmAddressHeader(
    label: String? = null,
    value: String? = null,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    val resolvedLabel = label ?: strings.homeCashierLabel
    val resolvedValue = value ?: strings.homeCashierValue
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = SahmSpacing.lg, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SahmSpacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(BrandPrimaryContainer, RoundedCornerShape(SahmRadius.sm)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Rounded.LocationOn,
                contentDescription = null,
                tint = BrandPrimary,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                resolvedLabel,
                style = MaterialTheme.typography.labelMedium,
                color = Neutral80,
            )
            Text(
                resolvedValue,
                style = MaterialTheme.typography.titleSmall,
                color = Neutral95,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(BrandPrimaryContainer, RoundedCornerShape(SahmRadius.sm)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Rounded.KeyboardArrowDown,
                contentDescription = null,
                tint = BrandPrimary,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
