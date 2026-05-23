package com.sahmfood.pos.presentation.ai

import com.sahmfood.pos.domain.repositories.OrderRepository
import com.sahmfood.pos.domain.services.AppClock
import com.sahmfood.pos.domain.services.IdGenerator
import com.sahmfood.pos.presentation.common.BaseStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

/**
 * AI assistant store. Plaza's real implementation calls an LLM backend.
 * Ours runs a local rule-based "AI" over the order repository so the
 * cashier gets useful answers offline:
 *
 * - "best sellers / popular / top" → query order history, count items
 * - "revenue / total / sales / today" → sum today's orders
 * - "pending / preparing / kitchen / orders" → count unsynced or recent orders
 * - "slowest / worst / least"  → least-frequent products in history
 *
 * Anything else returns a friendly fallback that still feels conversational.
 */
class AiChatStore(
    private val orderRepository: OrderRepository,
    private val clock: AppClock,
    private val ids: IdGenerator,
) : BaseStore<AiChatState, AiChatIntent, AiChatEffect>(AiChatState()) {

    init {
        // Seed with a greeting
        updateState {
            it.copy(
                messages = listOf(
                    AiMessage(
                        id = ids.newId(),
                        role = AiRole.Assistant,
                        content = "Hi! I'm your Sahm AI assistant. Ask me about today's sales, popular items, or pending orders.",
                        timestampMs = clock.nowMillis(),
                    )
                )
            )
        }
    }

    override suspend fun handle(intent: AiChatIntent) {
        when (intent) {
            is AiChatIntent.Send -> respond(intent.text)
            is AiChatIntent.QuickAction -> respond(intent.prompt)
            AiChatIntent.Clear -> updateState { it.copy(messages = emptyList(), isTyping = false) }
        }
    }

    private suspend fun respond(prompt: String) {
        val trimmed = prompt.trim()
        if (trimmed.isEmpty()) return
        val now = clock.nowMillis()
        // Append user message
        updateState {
            it.copy(
                messages = it.messages + AiMessage(
                    id = ids.newId(),
                    role = AiRole.User,
                    content = trimmed,
                    timestampMs = now,
                ),
                isTyping = true,
            )
        }
        // Mock "thinking" delay
        delay(700)
        // Compute reply
        val reply = try {
            generateReply(trimmed)
        } catch (t: Throwable) {
            "Sorry — I couldn't compute that right now."
        }
        updateState {
            it.copy(
                messages = it.messages + AiMessage(
                    id = ids.newId(),
                    role = AiRole.Assistant,
                    content = reply,
                    timestampMs = clock.nowMillis(),
                ),
                isTyping = false,
            )
        }
    }

    private suspend fun generateReply(prompt: String): String {
        val q = prompt.lowercase()
        val orders = orderRepository.observeHistory().first()
        val intent = classify(q)
        return when (intent) {
            QueryIntent.Revenue -> {
                val totalPiastres = orders.sumOf { it.grandTotal.amount }
                val major = totalPiastres / 100
                val cents = (totalPiastres % 100).toString().padStart(2, '0')
                "Today's revenue across ${orders.size} order${plural(orders.size)} is EGP $major.$cents."
            }
            QueryIntent.OrderCount -> {
                "There ${if (orders.size == 1) "is 1 order" else "are ${orders.size} orders"} on file today."
            }
            QueryIntent.BestSellers -> {
                val ranking = topItems(orders, n = 3, ascending = false)
                if (ranking.isEmpty()) "No items have been sold yet today."
                else "Top sellers today: " + ranking.joinToString(", ") { "${it.first} (${it.second} sold)" } + "."
            }
            QueryIntent.WorstSellers -> {
                val ranking = topItems(orders, n = 3, ascending = true)
                if (ranking.isEmpty()) "Not enough data to rank items yet."
                else "Slowest movers: " + ranking.joinToString(", ") { "${it.first} (${it.second} sold)" } + "."
            }
            QueryIntent.PendingOrders -> {
                val pending = orders.count {
                    it.status.name in listOf("PAID", "SYNC_PENDING", "SYNC_FAILED")
                }
                if (pending == 0) "Nothing pending — all orders are synced. ✓"
                else "$pending order${plural(pending)} still pending sync to the backend."
            }
            QueryIntent.Help -> {
                "I can answer questions about:\n• today's revenue & order count\n• best-selling or slowest items\n• pending or unsynced orders\nTry tapping one of the quick actions above."
            }
            QueryIntent.Greeting -> {
                "Hello! How can I help with today's shift?"
            }
            QueryIntent.Unknown -> {
                "I didn't quite catch that. Try asking about revenue, popular items, or pending orders — or tap a quick action."
            }
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

    private suspend fun topItems(
        orders: List<com.sahmfood.pos.domain.entities.Order>,
        n: Int,
        ascending: Boolean,
    ): List<Pair<String, Int>> {
        val counts = mutableMapOf<String, Int>()
        orders.forEach { order ->
            orderRepository.getItems(order.id).forEach { item ->
                counts[item.productName] = (counts[item.productName] ?: 0) + item.quantity
            }
        }
        val sorted = counts.entries.sortedBy { it.value }
        val ordered = if (ascending) sorted else sorted.reversed()
        return ordered.take(n).map { it.key to it.value }
    }

    private fun plural(n: Int) = if (n == 1) "" else "s"
}
