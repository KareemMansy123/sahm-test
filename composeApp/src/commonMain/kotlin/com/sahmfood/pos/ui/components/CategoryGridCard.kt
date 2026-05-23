package com.sahmfood.pos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing
import com.sahmfood.pos.ui.theme.categoryPastel

/**
 * Large category card used in the Categories tab grid. Pastel-tinted
 * background, big circular icon, category name, and item count.
 */
@Composable
fun CategoryGridCard(
    label: String,
    category: String?,
    pastelIndex: Int,
    itemCount: Int,
    onClick: () -> Unit,
) {
    val pastel = categoryPastel(pastelIndex)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(SahmRadius.lg),
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.08f),
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(SahmRadius.lg),
        color = pastel,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = SahmSpacing.xl, horizontal = SahmSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(Color.White.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = categoryIcon(category),
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(36.dp),
                )
            }
            Spacer(Modifier.height(SahmSpacing.md))
            Text(
                label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold, fontSize = 15.sp),
                color = Neutral95,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            Spacer(Modifier.height(2.dp))
            val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
            Text(
                if (itemCount == 1) strings.itemCountOne else strings.itemCountMany(itemCount),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = Neutral60,
            )
        }
    }
}
