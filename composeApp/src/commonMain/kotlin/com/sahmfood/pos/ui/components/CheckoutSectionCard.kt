package com.sahmfood.pos.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing
import com.sahmfood.pos.ui.theme.sahmCardShadowRaised

/**
 * Sahm checkout section card. Standardized white card with a brand-colored
 * leading icon, bold title, optional trailing action, and arbitrary content.
 */
@Composable
fun CheckoutSectionCard(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .sahmCardShadowRaised(shape = RoundedCornerShape(SahmRadius.lg)),
        shape = RoundedCornerShape(SahmRadius.lg),
        color = Color.White,
    ) {
        Column(modifier = Modifier.padding(SahmSpacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.size(SahmSpacing.sm))
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    ),
                    color = Neutral95,
                    modifier = Modifier.weight(1f),
                )
                if (trailing != null) trailing()
            }
            Spacer(Modifier.height(SahmSpacing.lg))
            content()
        }
    }
}
