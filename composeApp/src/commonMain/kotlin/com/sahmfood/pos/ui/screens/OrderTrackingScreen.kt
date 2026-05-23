package com.sahmfood.pos.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeliveryDining
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.ui.components.OrderStep
import com.sahmfood.pos.ui.components.PlazaOrderTracker
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.Neutral20
import com.sahmfood.pos.ui.theme.Neutral5
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing
import com.sahmfood.pos.ui.theme.SahmSuccess
import com.sahmfood.pos.ui.theme.SahmWarning
import com.sahmfood.pos.ui.theme.plazaCardShadowRaised
import kotlinx.coroutines.delay

/**
 * Plaza-inspired order tracking — for a POS this maps to the kitchen
 * preparation status. Steps: Received → Preparing → Ready.
 *
 * The current step gets a glow ring (the Plaza signature). On entry, the
 * screen auto-advances every 4 seconds to demo the tracker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    orderId: String,
    onBack: () -> Unit,
) {
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    val steps = remember(strings) {
        listOf(
            OrderStep(strings.trackingStageReceived, Icons.Rounded.Restaurant),
            OrderStep(strings.trackingStagePreparing, Icons.Rounded.LocalDining),
            OrderStep(strings.trackingStageReady, Icons.Rounded.CheckCircle),
        )
    }
    var currentStep by remember { mutableIntStateOf(0) }
    LaunchedEffect(orderId) {
        currentStep = 0
        while (currentStep < steps.lastIndex) {
            delay(4000)
            currentStep++
        }
    }

    Scaffold(
        containerColor = Neutral5,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        strings.trackingTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBackIos,
                            contentDescription = strings.commonBack,
                            tint = Neutral95,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // Map-like illustration banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Neutral20),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Rounded.DeliveryDining,
                        contentDescription = null,
                        tint = Neutral60,
                        modifier = Modifier.size(56.dp),
                    )
                    Spacer(Modifier.height(SahmSpacing.sm))
                    Text(
                        strings.trackingLiveStatus,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = Neutral60,
                    )
                }
            }
            // Progress card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SahmSpacing.lg)
                    .plazaCardShadowRaised(shape = RoundedCornerShape(SahmRadius.lg)),
                shape = RoundedCornerShape(SahmRadius.lg),
                color = Color.White,
            ) {
                Column(modifier = Modifier.padding(SahmSpacing.xl)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            strings.historyOrderHashPrefix + orderId.takeLast(6).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                            ),
                            color = Neutral95,
                        )
                        StatusPill(currentStep = currentStep)
                    }
                    Spacer(Modifier.height(SahmSpacing.xl))
                    PlazaOrderTracker(steps = steps, currentIndex = currentStep)
                    Spacer(Modifier.height(SahmSpacing.xl))
                    HorizontalDivider(color = Neutral20, thickness = 1.dp)
                    Spacer(Modifier.height(SahmSpacing.lg))
                    InfoRow(
                        icon = Icons.Rounded.AccessTime,
                        label = strings.trackingEstimatedTime,
                        value = strings.trackingEstimatedTimeValue,
                    )
                    Spacer(Modifier.height(SahmSpacing.md))
                    InfoRow(
                        icon = Icons.Rounded.Update,
                        label = strings.trackingLastUpdate,
                        value = when (currentStep) {
                            0 -> strings.trackingStatusReceived
                            1 -> strings.trackingStatusPreparing
                            else -> strings.trackingStatusReady
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusPill(currentStep: Int) {
    val strings = com.sahmfood.pos.ui.strings.LocalSahmStrings.current
    val (label, color) = when (currentStep) {
        0 -> strings.trackingStageReceived to SahmWarning
        1 -> strings.trackingStagePreparing to BrandPrimary
        else -> strings.trackingStageReady to SahmSuccess
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.10f),
        border = BorderStroke(1.5.dp, color),
    ) {
        Box(
            modifier = Modifier.padding(horizontal = SahmSpacing.md, vertical = 6.dp),
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                ),
                color = color,
            )
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            icon,
            contentDescription = null,
            tint = Neutral60,
            modifier = Modifier.size(20.dp).padding(top = 2.dp),
        )
        Spacer(Modifier.width(SahmSpacing.md))
        Column {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                color = Neutral60,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                ),
                color = Neutral95,
            )
        }
    }
}

private fun stageLabel(step: Int): String = when (step) {
    0 -> "Order received by kitchen"
    1 -> "Kitchen is preparing your order"
    else -> "Order is ready for pickup"
}
