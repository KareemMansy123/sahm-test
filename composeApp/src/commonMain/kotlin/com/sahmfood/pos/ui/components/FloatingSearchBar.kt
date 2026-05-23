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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmDimens
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing

@Composable
fun FloatingSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onScanClick: () -> Unit = {},
    placeholder: String = "Search menu…",
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(SahmDimens.searchBarHeight)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(SahmRadius.pill), clip = false),
        shape = RoundedCornerShape(SahmRadius.pill),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = SahmSpacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.Search,
                contentDescription = null,
                tint = Neutral60,
                modifier = Modifier.size(20.dp)
            )
            Box(modifier = Modifier.weight(1f).padding(horizontal = SahmSpacing.md)) {
                if (value.isEmpty()) {
                    Text(
                        placeholder,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Neutral60
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Neutral95),
                    cursorBrush = SolidColor(BrandPrimary),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            IconButton(onClick = onScanClick, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Rounded.QrCodeScanner,
                    contentDescription = "Scan barcode",
                    tint = BrandPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
