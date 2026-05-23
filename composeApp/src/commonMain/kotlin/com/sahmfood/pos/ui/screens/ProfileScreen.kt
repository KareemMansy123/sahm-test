package com.sahmfood.pos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SwitchAccount
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.presentation.settings.AppSettingsStore
import com.sahmfood.pos.ui.components.AboutSheet
import com.sahmfood.pos.ui.components.LanguagePickerSheet
import com.sahmfood.pos.ui.components.ThemePickerSheet
import com.sahmfood.pos.presentation.settings.AppTheme
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryContainer
import com.sahmfood.pos.ui.theme.BrandPrimaryLight
import com.sahmfood.pos.ui.theme.Neutral20
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral80
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmError
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    settings: AppSettingsStore,
    onOpenFavorites: () -> Unit,
    onOpenAi: () -> Unit,
    onOpenSwitchRegister: () -> Unit,
    onOpenPrinterSettings: () -> Unit,
    onOpenPreferences: () -> Unit,
    onOpenHelp: () -> Unit,
) {
    val theme by settings.theme.collectAsState()
    val language by settings.language.collectAsState()

    var showThemeSheet by remember { mutableStateOf(false) }
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showAboutSheet by remember { mutableStateOf(false) }
    val themeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val languageSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val aboutSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val scroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(BrandPrimary, BrandPrimaryLight)))
                .padding(top = SahmSpacing.xxxl, bottom = SahmSpacing.xxl),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.size(80.dp).background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("K", style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold, fontSize = 32.sp),
                        color = BrandPrimary)
                }
                Spacer(Modifier.size(SahmSpacing.md))
                Text(
                    "Kareem · Counter 1",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    color = Color.White,
                )
                Spacer(Modifier.size(2.dp))
                Text(
                    "Sahm Food · Cairo",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = Color.White.copy(alpha = 0.85f),
                )
            }
        }
        Spacer(Modifier.size(SahmSpacing.lg))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = SahmSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(SahmSpacing.md),
        ) {
            QuickStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.Favorite,
                label = "Favorites",
                onClick = onOpenFavorites,
            )
            QuickStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.AutoAwesome,
                label = "AI Assistant",
                onClick = onOpenAi,
            )
        }
        Spacer(Modifier.size(SahmSpacing.xl))

        SectionHeading("Account")
        SettingsRow(Icons.Rounded.SwitchAccount, "Switch register",
            onClick = onOpenSwitchRegister)
        SettingsRow(Icons.Rounded.Print, "Printer settings",
            onClick = onOpenPrinterSettings)
        SettingsRow(Icons.Rounded.Settings, "Preferences",
            onClick = onOpenPreferences)

        SectionHeading("App")
        SettingsRow(
            Icons.Rounded.Language,
            "Language",
            trailingValue = language.displayName,
            onClick = { showLanguageSheet = true },
        )
        SettingsRow(
            Icons.Rounded.DarkMode,
            "Theme",
            trailingValue = theme.displayName(),
            onClick = { showThemeSheet = true },
        )
        SettingsRow(Icons.Rounded.HelpOutline, "Help & support",
            onClick = onOpenHelp)
        SettingsRow(
            Icons.Rounded.Info,
            "About Sahm POS",
            trailingValue = "v1.0",
            onClick = { showAboutSheet = true },
        )

        SectionHeading("Session")
        SettingsRow(
            icon = Icons.Rounded.Logout,
            label = "End shift",
            tint = SahmError,
            onClick = { /* user opted to leave this unwired */ },
        )
        Spacer(Modifier.size(SahmSpacing.xxxl))
    }

    if (showThemeSheet) {
        ThemePickerSheet(
            current = theme,
            sheetState = themeSheetState,
            onPick = { picked ->
                settings.setTheme(picked)
                showThemeSheet = false
            },
            onDismiss = { showThemeSheet = false },
        )
    }
    if (showLanguageSheet) {
        LanguagePickerSheet(
            current = language,
            sheetState = languageSheetState,
            onPick = { picked ->
                settings.setLanguage(picked)
                showLanguageSheet = false
            },
            onDismiss = { showLanguageSheet = false },
        )
    }
    if (showAboutSheet) {
        AboutSheet(
            sheetState = aboutSheetState,
            onDismiss = { showAboutSheet = false },
            onOpenLink = { /* future: PlatformBrowser */ },
        )
    }
}

private fun AppTheme.displayName(): String = when (this) {
    AppTheme.Light -> "Light"
    AppTheme.Dark -> "Dark"
    AppTheme.System -> "System"
}

@Composable
private fun QuickStatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(SahmRadius.md),
        color = BrandPrimaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(SahmSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(BrandPrimary.copy(alpha = 0.20f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = BrandPrimary,
                    modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(SahmSpacing.md))
            Text(
                label,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                color = Neutral95,
            )
        }
    }
}

@Composable
private fun SectionHeading(text: String) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold, fontSize = 11.sp,
            letterSpacing = 1.sp),
        color = Neutral60,
        modifier = Modifier.padding(
            start = SahmSpacing.lg, top = SahmSpacing.md, bottom = SahmSpacing.sm),
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    trailingValue: String? = null,
    tint: Color = Neutral80,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SahmSpacing.lg, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(SahmRadius.md),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier.padding(SahmSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Neutral20, RoundedCornerShape(SahmRadius.sm)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = tint,
                    modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(SahmSpacing.md))
            Text(
                label,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                color = if (tint == SahmError) tint else Neutral95,
                modifier = Modifier.weight(1f),
            )
            if (trailingValue != null) {
                Text(
                    trailingValue,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Neutral60,
                )
                Spacer(Modifier.width(SahmSpacing.sm))
            }
            Icon(
                Icons.AutoMirrored.Rounded.ArrowForwardIos,
                contentDescription = null,
                tint = Neutral60,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}
