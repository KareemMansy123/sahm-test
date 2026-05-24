package com.sahmfood.pos.presentation.ai

enum class AiRole { User, Assistant }

data class AiMessage(
    val id: String,
    val role: AiRole,
    val content: String,
    val timestampMs: Long,
)

/**
 * UI-resolved label/prompt is decided at render time from [SahmStrings] using
 * [key]. We keep only the stable key here so the state survives language
 * switches without re-emitting a new list.
 */
data class QuickAction(val key: String)

data class AiChatState(
    val messages: List<AiMessage> = emptyList(),
    val isTyping: Boolean = false,
    val quickActions: List<QuickAction> = defaultQuickActions,
)

val defaultQuickActions = listOf(
    QuickAction("recommend"),
    QuickAction("search_burger"),
    QuickAction("best_sellers"),
    QuickAction("todays_revenue"),
    QuickAction("pending_orders"),
    QuickAction("slowest_item"),
)

sealed interface AiChatIntent {
    data class Send(val text: String) : AiChatIntent
    data class QuickAction(val prompt: String) : AiChatIntent
    data object Clear : AiChatIntent
}

sealed interface AiChatEffect {
    data class ShowError(val message: String) : AiChatEffect
}
