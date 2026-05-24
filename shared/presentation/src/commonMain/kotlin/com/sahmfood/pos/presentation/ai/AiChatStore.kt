package com.sahmfood.pos.presentation.ai

import com.sahmfood.pos.domain.entities.ChatMessage
import com.sahmfood.pos.domain.entities.ChatRole
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.domain.services.AppClock
import com.sahmfood.pos.domain.services.IdGenerator
import com.sahmfood.pos.domain.usecases.AddProductToCart
import com.sahmfood.pos.domain.usecases.ClearChatHistory
import com.sahmfood.pos.domain.usecases.CountPendingSyncOrders
import com.sahmfood.pos.domain.usecases.FindProductByName
import com.sahmfood.pos.domain.usecases.GetTodayRevenueSummary
import com.sahmfood.pos.domain.usecases.ObserveChatMessages
import com.sahmfood.pos.domain.usecases.RankItemsByVolume
import com.sahmfood.pos.domain.usecases.RecommendProducts
import com.sahmfood.pos.domain.usecases.SaveChatMessage
import com.sahmfood.pos.domain.usecases.SearchCatalog
import com.sahmfood.pos.domain.usecases.SnapshotCart
import com.sahmfood.pos.presentation.common.BaseStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * AI chat store. All data comes from use cases — no repository imports.
 *
 * Capabilities (the assistant is intentionally local — no LLM round-trip,
 * deterministic, runs offline):
 *
 *  - Recommend products ("what should I order?", "best burger?")
 *  - Search the catalog ("do you have pizza?", "find me something cold")
 *  - Describe a product ("tell me about double stack")
 *  - Add to cart from chat ("add a double stack", "order me a margherita")
 *  - Sales analytics (today's revenue, order count, best/slowest sellers)
 *  - Sync status (how many orders pending)
 *
 * Conversations are persisted via [SaveChatMessage] and observed via
 * [ObserveChatMessages], so the chat history survives app restart.
 */
class AiChatStore(
    private val observeChatMessages: ObserveChatMessages,
    private val saveChatMessage: SaveChatMessage,
    private val clearChatHistory: ClearChatHistory,
    private val getTodayRevenueSummary: GetTodayRevenueSummary,
    private val countPendingSyncOrders: CountPendingSyncOrders,
    private val rankItemsByVolume: RankItemsByVolume,
    private val searchCatalog: SearchCatalog,
    private val recommendProducts: RecommendProducts,
    private val findProductByName: FindProductByName,
    private val addProductToCart: AddProductToCart,
    private val snapshotCart: SnapshotCart,
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
        return when (val intent = classify(q)) {
            is QueryIntent.AddToCart -> handleAddToCart(intent.itemQuery)
            is QueryIntent.Describe -> handleDescribe(intent.itemQuery)
            is QueryIntent.Search -> handleSearch(intent.searchQuery)
            is QueryIntent.Recommend -> handleRecommend(intent.category)
            QueryIntent.Revenue -> handleRevenue()
            QueryIntent.OrderCount -> handleOrderCount()
            QueryIntent.BestSellers -> handleBestSellers()
            QueryIntent.WorstSellers -> handleWorstSellers()
            QueryIntent.PendingOrders -> handlePending()
            QueryIntent.Help -> HELP_TEXT
            QueryIntent.Greeting -> "Hello! What can I get for you today?"
            QueryIntent.Unknown ->
                "I didn't quite catch that. Try: \"recommend something\", " +
                "\"find me a burger\", \"tell me about pepperoni pizza\", " +
                "\"add a double stack to my cart\", or ask about today's sales."
        }
    }

    // ---- intent handlers ----

    private suspend fun handleRecommend(category: String?): String {
        val picks = recommendProducts(category = category, take = 3)
        if (picks.isEmpty()) {
            return if (category != null)
                "I don't see any available $category right now."
            else
                "The menu seems empty — try seeding the catalog first."
        }
        val header = if (category != null)
            "Top picks for $category:"
        else
            "Here's what I'd recommend right now:"
        return buildString {
            appendLine(header)
            picks.forEachIndexed { i, p ->
                appendLine("${i + 1}. ${p.name} (${p.category}) — ${p.price.toDisplayString()}")
                val desc = p.description.ifBlank { "Freshly prepared, made to order." }
                appendLine("   $desc")
            }
            append("Want me to add one? Just say \"add ${picks.first().name}\".")
        }
    }

    private suspend fun handleSearch(query: String): String {
        val hits = searchCatalog(query, take = 5)
        if (hits.isEmpty()) {
            return "I couldn't find anything matching \"$query\". " +
                "Try a different word or ask me to recommend something."
        }
        return buildString {
            appendLine("Found ${hits.size} match${if (hits.size == 1) "" else "es"} for \"$query\":")
            hits.forEach { p ->
                appendLine("• ${p.name} — ${p.category} — ${p.price.toDisplayString()}")
            }
        }
    }

    private suspend fun handleDescribe(query: String): String {
        val product = findProductByName(query)
            ?: return "I couldn't find a product matching \"$query\". " +
                "Try the exact name, or ask me to search instead."
        return buildString {
            appendLine("${product.name} (${product.category})")
            appendLine("Price: ${product.price.toDisplayString()}")
            val desc = product.description.ifBlank {
                "${product.name} is one of our ${product.category.lowercase()} offerings — " +
                    "freshly prepared and made to order."
            }
            appendLine(desc)
            append(
                if (product.isAvailable) "✓ Available now — say \"add ${product.name}\" to drop it in your cart."
                else "✗ Currently unavailable."
            )
        }
    }

    private suspend fun handleAddToCart(query: String): String {
        val product = findProductByName(query)
            ?: return "I couldn't find a product matching \"$query\" to add. " +
                "Try \"search for $query\" first."
        if (!product.isAvailable) {
            return "${product.name} is currently unavailable, so I didn't add it."
        }
        val currentLines = snapshotCart()
        addProductToCart(product.id, currentLines)
        val qty = (currentLines.firstOrNull { it.productId == product.id }?.quantity ?: 0) + 1
        return "Added **${product.name}** to your cart (now ×$qty) — " +
            "${product.price.toDisplayString()} per item."
    }

    private suspend fun handleRevenue(): String {
        val summary = getTodayRevenueSummary()
        val major = summary.totalPiastres / 100
        val cents = (summary.totalPiastres % 100).toString().padStart(2, '0')
        return "Today's revenue across ${summary.orderCount} order${plural(summary.orderCount)} " +
            "is EGP $major.$cents."
    }

    private suspend fun handleOrderCount(): String {
        val summary = getTodayRevenueSummary()
        return "There ${if (summary.orderCount == 1) "is 1 order" else "are ${summary.orderCount} orders"} " +
            "on file today."
    }

    private suspend fun handleBestSellers(): String {
        val ranking = rankItemsByVolume(ascending = false, take = 3)
        return if (ranking.isEmpty()) "No items have been sold yet today."
        else "Top sellers today: " + ranking.joinToString(", ") { "${it.first} (${it.second} sold)" } + "."
    }

    private suspend fun handleWorstSellers(): String {
        val ranking = rankItemsByVolume(ascending = true, take = 3)
        return if (ranking.isEmpty()) "Not enough data to rank items yet."
        else "Slowest movers: " + ranking.joinToString(", ") { "${it.first} (${it.second} sold)" } + "."
    }

    private suspend fun handlePending(): String {
        val pending = countPendingSyncOrders()
        return if (pending == 0) "Nothing pending — all orders are synced. ✓"
        else "$pending order${plural(pending)} still pending sync to the backend."
    }

    // ---- intent classification ----

    private sealed interface QueryIntent {
        data object Revenue : QueryIntent
        data object OrderCount : QueryIntent
        data object BestSellers : QueryIntent
        data object WorstSellers : QueryIntent
        data object PendingOrders : QueryIntent
        data object Help : QueryIntent
        data object Greeting : QueryIntent
        data object Unknown : QueryIntent
        data class Recommend(val category: String?) : QueryIntent
        data class Search(val searchQuery: String) : QueryIntent
        data class Describe(val itemQuery: String) : QueryIntent
        data class AddToCart(val itemQuery: String) : QueryIntent
    }

    private fun classify(q: String): QueryIntent {
        val tokens = q.split(' ', '?', '!', '.', ',').filter { it.isNotEmpty() }
        fun any(vararg words: String) = words.any { it in tokens }

        return when {
            // Greetings first so "hi can you recommend" still routes to greeting
            tokens.size <= 2 && any("hi", "hello", "hey", "salam") -> QueryIntent.Greeting

            any("help", "what", "can") && any("ask", "do") -> QueryIntent.Help

            // Add to cart: "add X", "order X", "put X in"
            any("add", "order") && tokens.size > 1 ->
                QueryIntent.AddToCart(extractAfter(q, listOf("add", "order")))
            any("put") && any("cart", "basket") ->
                QueryIntent.AddToCart(extractBetween(q, "put", "in"))

            // Describe a specific product: "tell me about X", "describe X", "what is X"
            (any("tell") && any("about")) || any("describe") ||
                (any("what") && any("is")) ->
                QueryIntent.Describe(extractAfter(q, listOf("about", "describe", "is")))

            // Recommend: "what should I order", "recommend", "best X", "give me the best"
            any("recommend", "suggest") ||
                (any("what", "which") && any("should", "best")) ->
                QueryIntent.Recommend(detectCategory(tokens))

            // Best / worst sellers (analytics) — only when there's no item name attached
            any("best", "popular", "top", "selling") && tokens.size <= 3 ->
                QueryIntent.BestSellers
            any("slowest", "worst", "least") -> QueryIntent.WorstSellers

            // Search: "find X", "search X", "do you have X", "show me X"
            any("find", "search", "show", "have") && tokens.size > 1 ->
                QueryIntent.Search(extractAfter(q, listOf("find", "search", "show", "have", "me")))

            // Sales analytics
            any("revenue", "sales", "total", "earned", "money") -> QueryIntent.Revenue
            any("orders", "many") && !any("pending", "preparing") -> QueryIntent.OrderCount
            any("pending", "preparing", "kitchen", "unsynced") -> QueryIntent.PendingOrders

            else -> QueryIntent.Unknown
        }
    }

    /** Best-effort: find one of the seeded category names in the user query. */
    private fun detectCategory(tokens: List<String>): String? {
        val knownCats = listOf("burger", "burgers", "pizza", "drink", "drinks",
            "side", "sides", "dessert", "desserts")
        val hit = tokens.firstOrNull { it in knownCats } ?: return null
        return when (hit) {
            "burger", "burgers" -> "Burgers"
            "pizza" -> "Pizza"
            "drink", "drinks" -> "Drinks"
            "side", "sides" -> "Sides"
            "dessert", "desserts" -> "Desserts"
            else -> null
        }
    }

    /** Returns everything after the last hit of [markers] in [q], trimmed. */
    private fun extractAfter(q: String, markers: List<String>): String {
        var idx = -1
        for (m in markers) {
            val i = q.lastIndexOf(m)
            if (i > idx) idx = i + m.length
        }
        if (idx <= 0) return q
        return q.substring(idx).trim().trimEnd('?', '!', '.', ',')
    }

    /** Returns the substring between [start] and [end] markers. */
    private fun extractBetween(q: String, start: String, end: String): String {
        val s = q.indexOf(start)
        val e = q.indexOf(end, startIndex = s + start.length)
        return if (s < 0 || e < 0) q.substringAfter(start, q).trim()
        else q.substring(s + start.length, e).trim()
    }

    // ---- helpers ----

    private fun greeting() = AiMessage(
        id = "greeting-${ids.newId()}",
        role = AiRole.Assistant,
        content = "Hi! I'm your Sahm AI assistant. I can recommend dishes, search the menu, " +
            "describe any product, add things to your cart, or summarise today's sales. " +
            "Try one of the chips below.",
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

    companion object {
        private const val HELP_TEXT = """I can help with:
• Recommend products — "what should I order?" / "best burger?"
• Search the menu — "do you have pizza?" / "find me cold drinks"
• Describe a product — "tell me about double stack"
• Add to cart — "add a double stack" / "order me a margherita"
• Today's sales — "what's today's revenue?" / "how many orders?"
• Best / slowest sellers — "what's selling well today?"
• Sync status — "any pending orders?"
Or tap a quick action above."""
    }
}
