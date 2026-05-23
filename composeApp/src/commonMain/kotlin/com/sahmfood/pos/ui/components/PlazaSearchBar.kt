package com.sahmfood.pos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing

/**
 * Plaza's clean white search bar — 8dp radius, soft shadow, leading search
 * icon and a trailing barcode-scan affordance in brand color.
 */
@Composable
fun PlazaSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String? = null,
    onScanClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val resolvedPlaceholder = placeholder
        ?: com.sahmfood.pos.ui.strings.LocalSahmStrings.current.homeSearchPlaceholder
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(SahmRadius.sm),
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.06f),
            ),
        shape = RoundedCornerShape(SahmRadius.sm),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = SahmSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Rounded.Search,
                contentDescription = null,
                tint = Neutral60,
                modifier = Modifier.size(20.dp),
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = SahmSpacing.md),
            ) {
                if (value.isEmpty()) {
                    Text(
                        resolvedPlaceholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Neutral60,
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Neutral95),
                    cursorBrush = SolidColor(BrandPrimary),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            IconButton(onClick = onScanClick, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Rounded.QrCodeScanner,
                    contentDescription = "Scan barcode",
                    tint = BrandPrimary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
