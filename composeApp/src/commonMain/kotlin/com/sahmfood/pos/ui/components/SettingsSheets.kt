package com.sahmfood.pos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Brightness7
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
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
import com.sahmfood.pos.presentation.settings.AppLanguage
import com.sahmfood.pos.presentation.settings.AppTheme
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryContainer
import com.sahmfood.pos.ui.theme.Neutral20
import com.sahmfood.pos.ui.theme.Neutral40
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePickerSheet(
    current: AppTheme,
    sheetState: SheetState,
    onPick: (AppTheme) -> Unit,
    onDismiss: () -> Unit,
) {
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = SahmRadius.xxl, topEnd = SahmRadius.xxl),
    ) {
        SheetHeader(strings.themePickerTitle)
        OptionRow(
            icon = Icons.Rounded.Brightness7,
            label = strings.themeLight,
            selected = current == AppTheme.Light,
            onClick = { onPick(AppTheme.Light) },
        )
        OptionRow(
            icon = Icons.Rounded.DarkMode,
            label = strings.themeDark,
            selected = current == AppTheme.Dark,
            onClick = { onPick(AppTheme.Dark) },
        )
        OptionRow(
            icon = Icons.Rounded.PhoneAndroid,
            label = strings.themeSystem,
            selected = current == AppTheme.System,
            onClick = { onPick(AppTheme.System) },
        )
        Spacer(Modifier.height(SahmSpacing.xl))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePickerSheet(
    current: AppLanguage,
    sheetState: SheetState,
    onPick: (AppLanguage) -> Unit,
    onDismiss: () -> Unit,
) {
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = SahmRadius.xxl, topEnd = SahmRadius.xxl),
    ) {
        SheetHeader(strings.languagePickerTitle)
        AppLanguage.entries.forEach { lang ->
            OptionRow(
                icon = Icons.Rounded.Translate,
                label = lang.displayName,
                selected = current == lang,
                onClick = { onPick(lang) },
            )
        }
        Spacer(Modifier.height(SahmSpacing.xl))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onOpenLink: (String) -> Unit,
) {
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = SahmRadius.xxl, topEnd = SahmRadius.xxl),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SahmSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(BrandPrimaryContainer, RoundedCornerShape(SahmRadius.lg)),
                contentAlignment = Alignment.Center,
            ) {
                Text("S",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold, fontSize = 36.sp),
                    color = BrandPrimary)
            }
            Spacer(Modifier.height(SahmSpacing.md))
            Text(strings.aboutAppName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold, fontSize = 22.sp),
                color = Neutral95)
            Spacer(Modifier.height(4.dp))
            Text(strings.aboutVersionLine,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = Neutral60)
            Spacer(Modifier.height(SahmSpacing.xl))
            InfoRow(strings.aboutBuildLabel, strings.aboutBuildValue)
            InfoRow(strings.aboutKotlinLabel, "2.0.21")
            InfoRow(strings.aboutComposeLabel, "1.7.0")
            InfoRow(strings.aboutSqlDelightLabel, "2.0.2")
            InfoRow(strings.aboutKoinLabel, "4.0.0")
            Spacer(Modifier.height(SahmSpacing.lg))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenLink("https://github.com/sahmfood/pos") },
                shape = RoundedCornerShape(SahmRadius.md),
                color = BrandPrimaryContainer,
            ) {
                Row(
                    modifier = Modifier.padding(SahmSpacing.lg),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(strings.aboutGithubLink,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                        color = BrandPrimary,
                        modifier = Modifier.weight(1f))
                    Icon(
                        Icons.Rounded.OpenInNew,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            Spacer(Modifier.height(SahmSpacing.lg))
            Text(strings.aboutCopyright,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = Neutral60)
        }
    }
}

@Composable
private fun SheetHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        ),
        color = Neutral95,
        modifier = Modifier.padding(
            start = SahmSpacing.xl,
            end = SahmSpacing.xl,
            top = SahmSpacing.sm,
            bottom = SahmSpacing.md,
        ),
    )
}

@Composable
private fun OptionRow(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SahmSpacing.lg, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(SahmRadius.md),
        color = if (selected) BrandPrimaryContainer else Color.White,
        border = if (selected)
            androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary)
        else
            androidx.compose.foundation.BorderStroke(1.dp, Neutral40),
    ) {
        Row(
            modifier = Modifier.padding(SahmSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        if (selected) BrandPrimary else Neutral20,
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (selected) Color.White else Neutral60,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.size(SahmSpacing.md))
            Text(
                label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                ),
                color = Neutral95,
                modifier = Modifier.weight(1f),
            )
            if (selected) {
                Icon(
                    Icons.Rounded.Check,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
            color = Neutral60)
        Text(value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
            color = Neutral95)
    }
}
