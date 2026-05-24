package com.sahmfood.pos.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.Neutral20
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmSpacing

/**
 * the app's signature 3-step order tracker. Each step is a 56dp filled
 * circle. Completed steps fill in brand color; the current step gets a
 * primary-colored glow ring; future steps are flat grey.
 */
data class OrderStep(
    val label: String,
    val icon: ImageVector,
)

@Composable
fun SahmOrderTracker(
    steps: List<OrderStep>,
    currentIndex: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            steps.forEachIndexed { idx, step ->
                StepCircle(
                    icon = step.icon,
                    state = when {
                        idx < currentIndex -> StepState.Completed
                        idx == currentIndex -> StepState.Current
                        else -> StepState.Future
                    },
                )
                if (idx < steps.lastIndex) {
                    StepConnector(completed = idx < currentIndex, modifier = Modifier.weight(1f))
                }
            }
        }
        Spacer(Modifier.height(SahmSpacing.md))
        Row(modifier = Modifier.fillMaxWidth()) {
            steps.forEachIndexed { idx, step ->
                Text(
                    step.label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (idx == currentIndex) FontWeight.Bold
                                     else FontWeight.Medium,
                    ),
                    color = when {
                        idx < currentIndex -> Neutral95
                        idx == currentIndex -> BrandPrimary
                        else -> Neutral60
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

private enum class StepState { Completed, Current, Future }

@Composable
private fun StepCircle(icon: ImageVector, state: StepState) {
    val bg by animateColorAsState(
        targetValue = when (state) {
            StepState.Completed, StepState.Current -> BrandPrimary
            StepState.Future -> Neutral20
        },
        animationSpec = tween(300),
        label = "step-bg",
    )
    val tint = when (state) {
        StepState.Completed, StepState.Current -> Color.White
        StepState.Future -> Neutral60
    }
    Box(
        modifier = Modifier
            .size(56.dp)
            .then(
                if (state == StepState.Current) {
                    Modifier.shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        ambientColor = BrandPrimary.copy(alpha = 0.35f),
                        spotColor = BrandPrimary.copy(alpha = 0.45f),
                    )
                } else Modifier,
            )
            .background(bg, CircleShape)
            .then(
                if (state == StepState.Current) {
                    Modifier.border(3.dp, BrandPrimary.copy(alpha = 0.30f), CircleShape)
                } else Modifier,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(26.dp),
        )
    }
}

@Composable
private fun StepConnector(completed: Boolean, modifier: Modifier) {
    val color by animateColorAsState(
        targetValue = if (completed) BrandPrimary else Neutral20,
        animationSpec = tween(300),
        label = "connector-color",
    )
    Box(
        modifier = modifier
            .padding(horizontal = SahmSpacing.xs)
            .height(4.dp)
            .background(color, RoundedCornerShape(2.dp)),
    )
}
