package com.sahmfood.pos.presentation.ai

enum class AiRole { User, Assistant }

data class AiMessage(
    val id: String,
    val role: AiRole,
    val content: String,
    val timestampMs: Long,
)

data class QuickAction(val label: String, val prompt: String)

data class AiChatState(
    val messages: List<AiMessage> = emptyList(),
    val isTyping: Boolean = false,
    val quickActions: List<QuickAction> = defaultQuickActions,
)

val defaultQuickActions = listOf(
    QuickAction("Best sellers today", "What are the best-selling items today?"),
    QuickAction("Pending orders", "How many orders are still being prepared?"),
    QuickAction("Today's revenue", "What is today's total revenue?"),
    QuickAction("Slowest item", "Which item sold the least today?"),
)

sealed interface AiChatIntent {
    data class Send(val text: String) : AiChatIntent
    data class QuickAction(val prompt: String) : AiChatIntent
    data object Clear : AiChatIntent
}

sealed interface AiChatEffect {
    data class ShowError(val message: String) : AiChatEffect
}
