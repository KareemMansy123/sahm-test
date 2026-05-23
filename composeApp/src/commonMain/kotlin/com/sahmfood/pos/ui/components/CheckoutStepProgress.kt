package com.sahmfood.pos.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.Neutral20
import com.sahmfood.pos.ui.theme.Neutral40
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmSpacing

@Composable
fun CheckoutStepProgress(
    currentStep: Int,
    steps: List<String>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SahmSpacing.xl, vertical = SahmSpacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { idx, label ->
            val state = when {
                idx < currentStep -> StepState.Completed
                idx == currentStep -> StepState.Active
                else -> StepState.Future
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StepCircle(stepNumber = idx + 1, state = state)
                Spacer(Modifier.size(SahmSpacing.xs))
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (state == StepState.Future) Neutral40 else Neutral95,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
            if (idx < steps.lastIndex) {
                StepConnector(completed = idx < currentStep)
            }
        }
    }
}

private enum class StepState { Completed, Active, Future }

@Composable
private fun StepCircle(stepNumber: Int, state: StepState) {
    val bg by animateColorAsState(
        targetValue = when (state) {
            StepState.Completed, StepState.Active -> BrandPrimary
            StepState.Future -> Neutral20
        },
        animationSpec = tween(300),
        label = "step-bg"
    )
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(bg, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            StepState.Completed -> Icon(
                Icons.Rounded.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            StepState.Active -> Text(
                stepNumber.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
            StepState.Future -> Text(
                stepNumber.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = Neutral60
            )
        }
    }
}

@Composable
private fun StepConnector(completed: Boolean) {
    val color by animateColorAsState(
        targetValue = if (completed) BrandPrimary else Neutral40,
        animationSpec = tween(300),
        label = "connector"
    )
    Box(
        modifier = Modifier
            .padding(horizontal = SahmSpacing.xs)
            .height(2.dp)
            .width(48.dp)
            .background(color, RoundedCornerShape(1.dp))
            .padding(bottom = 18.dp)
    )
}
