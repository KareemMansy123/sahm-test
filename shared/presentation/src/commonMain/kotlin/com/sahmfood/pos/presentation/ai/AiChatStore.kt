package com.sahmfood.pos.presentation.ai

import com.sahmfood.pos.domain.entities.ChatMessage
import com.sahmfood.pos.domain.entities.ChatRole
import com.sahmfood.pos.domain.services.AppClock
import com.sahmfood.pos.domain.services.IdGenerator
import com.sahmfood.pos.domain.usecases.ClearChatHistory
import com.sahmfood.pos.domain.usecases.CountPendingSyncOrders
import com.sahmfood.pos.domain.usecases.GetTodayRevenueSummary
import com.sahmfood.pos.domain.usecases.ObserveChatMessages
import com.sahmfood.pos.domain.usecases.RankItemsByVolume
import com.sahmfood.pos.domain.usecases.SaveChatMessage
import com.sahmfood.pos.presentation.common.BaseStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * AI chat store. All data comes from use cases — no repository imports.
 * Messages are persisted via [SaveChatMessage] and observed via
 * [ObserveChatMessages], so the conversation survives app restart.
 */
class AiChatStore(
    private val observeChatMessages: ObserveChatMessages,
    private val saveChatMessage: SaveChatMessage,
    private val clearChatHistory: ClearChatHistory,
    private val getTodayRevenueSummary: GetTodayRevenueSummary,
    private val countPendingSyncOrders: CountPendingSyncOrders,
    private val rankItemsByVolume: RankItemsByVolume,
    private val clock: AppClock,
    private val ids: IdGenerator,
) : BaseStore<AiChatState, AiChatIntent, AiChatEffect>(AiChatState()) {

    init {
        scope.launch {
            observeChatMessages().collect { persisted ->
                updateState {
                    it.copy(
                        messages = if (persisted.isEmpty()) listOf(greeting()) else persisted.map { m -> m.toUi() },
                    )
                }
            }
        }
    }

    override suspend fun handle(intent: AiChatIntent) {
        when (intent) {
            is AiChatIntent.Send -> respond(intent.text)
            is AiChatIntent.QuickAction -> respond(intent.prompt)
            AiChatIntent.Clear -> {
                clearChatHistory()
            }
        }
    }

    private suspend fun respond(prompt: String) {
        val trimmed = prompt.trim()
        if (trimmed.isEmpty()) return
        // Persist user message
        saveChatMessage(
            ChatMessage(
                id = ids.newId(),
                role = ChatRole.USER,
                content = trimmed,
                timestampMs = clock.nowMillis(),
            )
        )
        updateState { it.copy(isTyping = true) }
        delay(700)
        val reply = try {
            generateReply(trimmed)
        } catch (t: Throwable) {
            "Sorry — I couldn't compute that right now."
        }
        saveChatMessage(
            ChatMessage(
                id = ids.newId(),
                role = ChatRole.ASSISTANT,
                content = reply,
                timestampMs = clock.nowMillis(),
            )
        )
        updateState { it.copy(isTyping = false) }
    }

    private suspend fun generateReply(prompt: String): String {
        val q = prompt.lowercase()
        return when (classify(q)) {
            QueryIntent.Revenue -> {
                val summary = getTodayRevenueSummary()
                val major = summary.totalPiastres / 100
                val cents = (summary.totalPiastres % 100).toString().padStart(2, '0')
                "Today's revenue across ${summary.orderCount} order${plural(summary.orderCount)} is EGP $major.$cents."
            }
            QueryIntent.OrderCount -> {
                val summary = getTodayRevenueSummary()
                "There ${if (summary.orderCount == 1) "is 1 order" else "are ${summary.orderCount} orders"} on file today."
            }
            QueryIntent.BestSellers -> {
                val ranking = rankItemsByVolume(ascending = false, take = 3)
                if (ranking.isEmpty()) "No items have been sold yet today."
                else "Top sellers today: " + ranking.joinToString(", ") { "${it.first} (${it.second} sold)" } + "."
            }
            QueryIntent.WorstSellers -> {
                val ranking = rankItemsByVolume(ascending = true, take = 3)
                if (ranking.isEmpty()) "Not enough data to rank items yet."
                else "Slowest movers: " + ranking.joinToString(", ") { "${it.first} (${it.second} sold)" } + "."
            }
            QueryIntent.PendingOrders -> {
                val pending = countPendingSyncOrders()
                if (pending == 0) "Nothing pending — all orders are synced. ✓"
                else "$pending order${plural(pending)} still pending sync to the backend."
            }
            QueryIntent.Help -> {
                "I can answer questions about:\n• today's revenue & order count\n• best-selling or slowest items\n• pending or unsynced orders\nTry tapping one of the quick actions above."
            }
            QueryIntent.Greeting -> "Hello! How can I help with today's shift?"
            QueryIntent.Unknown -> "I didn't quite catch that. Try asking about revenue, popular items, or pending orders — or tap a quick action."
        }
    }

    private enum class QueryIntent {
        Revenue, OrderCount, BestSellers, WorstSellers, PendingOrders, Help, Greeting, Unknown
    }

    private fun classify(q: String): QueryIntent {
        val tokens = q.split(' ', '?', '!', '.', ',').filter { it.isNotEmpty() }
        fun any(vararg words: String) = words.any { it in tokens }
        return when {
            any("hi", "hello", "hey") -> QueryIntent.Greeting
            any("help", "what", "can") && any("ask", "do") -> QueryIntent.Help
            any("revenue", "sales", "total", "earned", "money") -> QueryIntent.Revenue
            any("orders", "many") && !any("pending", "preparing") -> QueryIntent.OrderCount
            any("best", "popular", "top", "selling") -> QueryIntent.BestSellers
            any("slowest", "worst", "least") -> QueryIntent.WorstSellers
            any("pending", "preparing", "kitchen", "unsynced") -> QueryIntent.PendingOrders
            else -> QueryIntent.Unknown
        }
    }

    private fun greeting() = AiMessage(
        id = "greeting-${ids.newId()}",
        role = AiRole.Assistant,
        content = "Hi! I'm your Sahm AI assistant. Ask me about today's sales, popular items, or pending orders.",
        timestampMs = clock.nowMillis(),
    )

    private fun ChatMessage.toUi(): AiMessage = AiMessage(
        id = id,
        role = when (role) {
            ChatRole.USER -> AiRole.User
            ChatRole.ASSISTANT -> AiRole.Assistant
        },
        content = content,
        timestampMs = timestampMs,
    )

    private fun plural(n: Int) = if (n == 1) "" else "s"
}
