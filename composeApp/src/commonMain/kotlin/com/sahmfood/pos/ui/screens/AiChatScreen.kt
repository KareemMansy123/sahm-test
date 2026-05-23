package com.sahmfood.pos.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.presentation.ai.AiChatIntent
import com.sahmfood.pos.presentation.ai.AiChatStore
import com.sahmfood.pos.presentation.ai.AiMessage
import com.sahmfood.pos.presentation.ai.AiRole
import com.sahmfood.pos.presentation.ai.QuickAction
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.BrandPrimaryLight
import com.sahmfood.pos.ui.theme.Neutral10
import com.sahmfood.pos.ui.theme.Neutral20
import com.sahmfood.pos.ui.theme.Neutral5
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral80
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmSpacing
import com.sahmfood.pos.ui.theme.SahmSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    store: AiChatStore,
    onBack: () -> Unit,
) {
    val state by store.state.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Scaffold(
        containerColor = Neutral5,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(BrandPrimaryLight, BrandPrimary),
                                    ),
                                    CircleShape,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                        Spacer(Modifier.width(SahmSpacing.md))
                        Column {
                            Text(
                                "AI Assistant",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                ),
                                color = Neutral95,
                            )
                            Text(
                                "Online",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = SahmSuccess,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBackIos,
                            contentDescription = "Back",
                            tint = Neutral95,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { store.dispatch(AiChatIntent.Clear) }) {
                        Icon(
                            Icons.Rounded.DeleteOutline,
                            contentDescription = "Clear chat",
                            tint = Neutral80,
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
                .imePadding(),
        ) {
            // Quick actions shown only when conversation is fresh
            if (state.messages.size <= 1) {
                Spacer(Modifier.height(SahmSpacing.md))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = SahmSpacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(SahmSpacing.sm),
                ) {
                    items(state.quickActions) { action ->
                        QuickActionChip(action) {
                            store.dispatch(AiChatIntent.QuickAction(action.prompt))
                        }
                    }
                }
                Spacer(Modifier.height(SahmSpacing.md))
            }
            // Message list
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(vertical = SahmSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(SahmSpacing.sm),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.messages, key = { it.id }) { message ->
                        ChatBubble(message)
                    }
                    if (state.isTyping) {
                        item { TypingIndicator() }
                    }
                }
            }
            ChatInput(
                onSend = { store.dispatch(AiChatIntent.Send(it)) },
                enabled = !state.isTyping,
            )
        }
    }
}

@Composable
private fun QuickActionChip(action: QuickAction, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.30f)),
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = SahmSpacing.lg, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = BrandPrimary,
                modifier = Modifier.size(14.dp),
            )
            Spacer(Modifier.width(SahmSpacing.sm))
            Text(
                action.label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                ),
                color = Neutral95,
            )
        }
    }
}

@Composable
private fun ChatBubble(message: AiMessage) {
    val isUser = message.role == AiRole.User
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SahmSpacing.lg, vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom,
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        Brush.linearGradient(listOf(BrandPrimaryLight, BrandPrimary)),
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
            }
            Spacer(Modifier.width(SahmSpacing.sm))
        }
        Column(horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (isUser) 20.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 20.dp,
                ),
                color = if (isUser) BrandPrimary else Color.White,
                shadowElevation = 1.dp,
            ) {
                Text(
                    message.content,
                    modifier = Modifier
                        .padding(horizontal = SahmSpacing.lg, vertical = 12.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 21.sp,
                    ),
                    color = if (isUser) Color.White else Neutral95,
                )
            }
        }
        if (isUser) {
            Spacer(Modifier.width(SahmSpacing.sm))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Neutral20, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("Y", style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold, fontSize = 13.sp), color = Neutral80)
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SahmSpacing.lg, vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    Brush.linearGradient(listOf(BrandPrimaryLight, BrandPrimary)),
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
        }
        Spacer(Modifier.width(SahmSpacing.sm))
        Surface(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 4.dp),
            color = Color.White,
            shadowElevation = 1.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = SahmSpacing.lg, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TypingDot(delay = 0)
                TypingDot(delay = 150)
                TypingDot(delay = 300)
            }
        }
    }
}

@Composable
private fun TypingDot(delay: Int) {
    val infinite = rememberInfiniteTransition(label = "typing-$delay")
    val alpha by infinite.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(600, delayMillis = delay),
            RepeatMode.Reverse,
        ),
        label = "alpha",
    )
    Box(
        modifier = Modifier
            .size(8.dp)
            .graphicsLayer { this.alpha = alpha }
            .background(BrandPrimary, CircleShape),
    )
}

@Composable
private fun ChatInput(
    onSend: (String) -> Unit,
    enabled: Boolean,
) {
    var text by remember { mutableStateOf("") }
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier.padding(SahmSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                color = Neutral10,
                border = BorderStroke(1.dp, Neutral20),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = SahmSpacing.lg, vertical = SahmSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (text.isEmpty()) {
                            Text(
                                "Ask me anything…",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                color = Neutral60,
                            )
                        }
                        BasicTextField(
                            value = text,
                            onValueChange = { text = it },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp, color = Neutral95),
                            cursorBrush = SolidColor(BrandPrimary),
                            singleLine = false,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            Spacer(Modifier.width(SahmSpacing.sm))
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (text.isBlank() || !enabled) Neutral20
                        else BrandPrimary,
                        CircleShape,
                    )
                    .clickable(enabled = text.isNotBlank() && enabled) {
                        onSend(text)
                        text = ""
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.Send,
                    contentDescription = "Send",
                    tint = if (text.isBlank() || !enabled) Neutral60 else Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
