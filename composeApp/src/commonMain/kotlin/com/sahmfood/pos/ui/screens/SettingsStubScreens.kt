package com.sahmfood.pos.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContactSupport
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryContainer
import com.sahmfood.pos.ui.theme.Neutral20
import com.sahmfood.pos.ui.theme.Neutral40
import com.sahmfood.pos.ui.theme.Neutral5
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral80
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing
import com.sahmfood.pos.ui.theme.SahmSuccess
import com.sahmfood.pos.ui.theme.pressScaleAuto

// ---- Switch Register ----

private data class Register(val id: String, val name: String, val location: String, val online: Boolean)

private val mockRegisters = listOf(
    Register("R001", "Counter 1", "Main Hall · Cairo", online = true),
    Register("R002", "Counter 2", "Main Hall · Cairo", online = true),
    Register("R003", "Drive-Thru", "Outdoor · Cairo", online = false),
    Register("R004", "Kiosk", "Mall Branch · 6th October", online = true),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchRegisterScreen(onBack: () -> Unit) {
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    var selected by remember { mutableStateOf("R001") }
    SettingsScaffold(title = strings.switchRegisterTitle, onBack = onBack) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(SahmSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(SahmSpacing.sm),
        ) {
            items(mockRegisters) { reg ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pressScaleAuto(pressedScale = 0.98f)
                        .clickable { selected = reg.id },
                    shape = RoundedCornerShape(SahmRadius.md),
                    color = if (selected == reg.id) BrandPrimaryContainer else Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (selected == reg.id) 2.dp else 1.dp,
                        color = if (selected == reg.id) BrandPrimary else Neutral40,
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(SahmSpacing.lg),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (selected == reg.id) BrandPrimary
                                    else Neutral20,
                                    CircleShape,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Rounded.Storefront,
                                contentDescription = null,
                                tint = if (selected == reg.id) Color.White else Neutral60,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                        Spacer(Modifier.width(SahmSpacing.md))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                reg.name,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
                                color = Neutral95,
                            )
                            Text(
                                reg.location,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = Neutral60,
                            )
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            if (reg.online) SahmSuccess else Neutral60,
                                            CircleShape,
                                        ),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    if (reg.online) strings.switchRegisterOnline else strings.switchRegisterOffline,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = if (reg.online) SahmSuccess else Neutral60,
                                )
                            }
                        }
                        if (selected == reg.id) {
                            Icon(
                                Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = BrandPrimary,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---- Printer Settings ----

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterSettingsScreen(onBack: () -> Unit) {
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    var autoPrint by remember { mutableStateOf(true) }
    var printLogo by remember { mutableStateOf(true) }
    var printCustomerCopy by remember { mutableStateOf(false) }

    SettingsScaffold(title = strings.printerTitle, onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(SahmSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(SahmSpacing.md),
        ) {
            // Connected printer card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(SahmRadius.md),
                color = Color.White,
            ) {
                Column(modifier = Modifier.padding(SahmSpacing.lg)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(BrandPrimaryContainer, RoundedCornerShape(SahmRadius.sm)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Rounded.Print, contentDescription = null,
                                tint = BrandPrimary, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(SahmSpacing.md))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Star TSP-100 (Mock)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold, fontSize = 15.sp),
                                color = Neutral95)
                            Text("80mm thermal · 192.168.1.42",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = Neutral60)
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    SahmSuccess.copy(alpha = 0.15f),
                                    RoundedCornerShape(SahmRadius.xs),
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Text(strings.printerConnected,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold, fontSize = 10.sp),
                                color = SahmSuccess)
                        }
                    }
                }
            }

            SectionLabel(strings.printerAvailable)
            PrinterRow(Icons.Rounded.Wifi, "Star TSP-100", "192.168.1.42", selected = true)
            PrinterRow(Icons.Rounded.Bluetooth, "Epson TM-T20III", "BT · paired")
            PrinterRow(Icons.Rounded.Usb, "Citizen CT-S310", "USB · /dev/ttyUSB0")

            SectionLabel(strings.printerPrintOptions)
            ToggleRow(
                label = strings.printerAutoPrint,
                description = strings.printerAutoPrintDesc,
                checked = autoPrint,
                onChange = { autoPrint = it },
            )
            ToggleRow(
                label = strings.printerIncludeLogo,
                description = strings.printerIncludeLogoDesc,
                checked = printLogo,
                onChange = { printLogo = it },
            )
            ToggleRow(
                label = strings.printerCustomerCopy,
                description = strings.printerCustomerCopyDesc,
                checked = printCustomerCopy,
                onChange = { printCustomerCopy = it },
            )

            Spacer(Modifier.height(SahmSpacing.lg))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .pressScaleAuto(pressedScale = 0.97f)
                    .clickable { /* test print */ },
                shape = RoundedCornerShape(SahmRadius.md),
                color = BrandPrimaryContainer,
            ) {
                Row(
                    modifier = Modifier.padding(SahmSpacing.lg),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.Print, contentDescription = null,
                        tint = BrandPrimary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(SahmSpacing.sm))
                    Text(strings.printerTestReceipt,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                        color = BrandPrimary)
                }
            }
        }
    }
}

// ---- Preferences ----

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(onBack: () -> Unit) {
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    var soundEnabled by remember { mutableStateOf(true) }
    var hapticEnabled by remember { mutableStateOf(true) }
    var confirmDelete by remember { mutableStateOf(true) }
    var showStockWarnings by remember { mutableStateOf(true) }
    var autoLogout by remember { mutableStateOf(false) }

    SettingsScaffold(title = strings.preferencesTitle, onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(SahmSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(SahmSpacing.sm),
        ) {
            SectionLabel(strings.preferencesInteraction)
            ToggleRow(
                label = strings.preferencesSoundFeedback,
                description = strings.preferencesSoundFeedbackDesc,
                checked = soundEnabled,
                onChange = { soundEnabled = it },
            )
            ToggleRow(
                label = strings.preferencesHaptic,
                description = strings.preferencesHapticDesc,
                checked = hapticEnabled,
                onChange = { hapticEnabled = it },
            )

            SectionLabel(strings.preferencesSafety)
            ToggleRow(
                label = strings.preferencesConfirmDelete,
                description = strings.preferencesConfirmDeleteDesc,
                checked = confirmDelete,
                onChange = { confirmDelete = it },
            )
            ToggleRow(
                label = strings.preferencesStockWarnings,
                description = strings.preferencesStockWarningsDesc,
                checked = showStockWarnings,
                onChange = { showStockWarnings = it },
            )

            SectionLabel(strings.preferencesSession)
            ToggleRow(
                label = strings.preferencesAutoLogout,
                description = strings.preferencesAutoLogoutDesc,
                checked = autoLogout,
                onChange = { autoLogout = it },
            )

            SectionLabel(strings.preferencesDefaults)
            InfoRowItem(strings.preferencesDefaultPayment, strings.preferencesDefaultPaymentValue)
            InfoRowItem(strings.preferencesDefaultTax, strings.preferencesDefaultTaxValue)
            InfoRowItem(strings.preferencesReceiptWidth, strings.preferencesReceiptWidthValue)
            InfoRowItem(strings.preferencesCurrency, strings.preferencesCurrencyValue)
        }
    }
}

// ---- Help & Support ----

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(onBack: () -> Unit) {
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    SettingsScaffold(title = strings.helpTitle, onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(SahmSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(SahmSpacing.sm),
        ) {
            SectionLabel(strings.helpGetInTouch)
            ContactRow(
                icon = Icons.Rounded.Phone,
                label = strings.helpCallSupport,
                value = strings.helpCallSupportValue,
            )
            ContactRow(
                icon = Icons.Rounded.Mail,
                label = strings.helpEmailSupport,
                value = strings.helpEmailSupportValue,
            )
            ContactRow(
                icon = Icons.Rounded.ContactSupport,
                label = strings.helpLiveChat,
                value = strings.helpLiveChatValue,
            )

            SectionLabel(strings.helpResources)
            LinkRow(
                icon = Icons.Rounded.MenuBook,
                label = strings.helpCashierHandbook,
                description = strings.helpCashierHandbookDesc,
            )
            LinkRow(
                icon = Icons.AutoMirrored.Rounded.HelpOutline,
                label = strings.helpTroubleshooting,
                description = strings.helpTroubleshootingDesc,
            )

            SectionLabel(strings.helpFaq)
            FaqRow(
                question = strings.helpFaqRefundQ,
                answer = strings.helpFaqRefundA,
            )
            FaqRow(
                question = strings.helpFaqPrinterQ,
                answer = strings.helpFaqPrinterA,
            )
            FaqRow(
                question = strings.helpFaqOfflineQ,
                answer = strings.helpFaqOfflineA,
            )
        }
    }
}

// ---- Building blocks ----

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
        containerColor = Neutral5,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold, fontSize = 18.sp),
                        color = Neutral95,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBackIos,
                            contentDescription = com.sahmfood.pos.ui.strings.LocalSahmStrings.current.commonBack,
                            tint = Neutral95,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            content()
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
        ),
        color = Neutral60,
        modifier = Modifier.padding(top = SahmSpacing.md, bottom = SahmSpacing.xs),
    )
}

@Composable
private fun ToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .pressScaleAuto(pressedScale = 0.98f)
            .clickable { onChange(!checked) },
        shape = RoundedCornerShape(SahmRadius.md),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier.padding(SahmSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                    color = Neutral95)
                Spacer(Modifier.height(2.dp))
                Text(description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Neutral60)
            }
            Switch(
                checked = checked,
                onCheckedChange = onChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = BrandPrimary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Neutral40,
                ),
            )
        }
    }
}

@Composable
private fun InfoRowItem(label: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SahmRadius.md),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier.padding(SahmSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = Neutral95)
            Text(value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                color = Neutral60)
        }
    }
}

@Composable
private fun PrinterRow(
    icon: ImageVector,
    name: String,
    detail: String,
    selected: Boolean = false,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* select */ },
        shape = RoundedCornerShape(SahmRadius.md),
        color = Color.White,
        border = if (selected)
            androidx.compose.foundation.BorderStroke(2.dp, BrandPrimary)
        else
            androidx.compose.foundation.BorderStroke(1.dp, Neutral40),
    ) {
        Row(
            modifier = Modifier.padding(SahmSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = Neutral80,
                modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(SahmSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                    color = Neutral95)
                Text(detail,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Neutral60)
            }
            if (selected) {
                Icon(Icons.Rounded.Check, contentDescription = null,
                    tint = BrandPrimary, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun ContactRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .pressScaleAuto(pressedScale = 0.98f)
            .clickable { /* dial / mail */ },
        shape = RoundedCornerShape(SahmRadius.md),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier.padding(SahmSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(BrandPrimaryContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = BrandPrimary,
                    modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(SahmSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                    color = Neutral95)
                Text(value,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Neutral60)
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

@Composable
private fun LinkRow(
    icon: ImageVector,
    label: String,
    description: String,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .pressScaleAuto(pressedScale = 0.98f)
            .clickable { },
        shape = RoundedCornerShape(SahmRadius.md),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier.padding(SahmSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Neutral20, RoundedCornerShape(SahmRadius.sm)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = Neutral80,
                    modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(SahmSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                    color = Neutral95)
                Text(description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Neutral60)
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

@Composable
private fun FaqRow(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .pressScaleAuto(pressedScale = 0.98f)
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(SahmRadius.md),
        color = Color.White,
    ) {
        Column(
            modifier = Modifier
                .padding(SahmSpacing.md)
                .animateContentSize(),
        ) {
            Text(question,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                color = Neutral95)
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(Modifier.height(6.dp))
                    Text(answer,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp, lineHeight = 19.sp),
                        color = Neutral60)
                }
            }
        }
    }
}
