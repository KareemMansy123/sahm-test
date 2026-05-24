package com.sahmfood.pos.domain.usecases

import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderStatus
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.domain.repositories.OrderRepository
import com.sahmfood.pos.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Read-only insights and catalog actions the AI assistant exposes. Each
 * lives as a single-purpose use case so the store calls a verb, not a
 * repository.
 */

data class RevenueSummary(val totalPiastres: Long, val orderCount: Int)

class GetTodayRevenueSummary(private val orderRepository: OrderRepository) {
    suspend operator fun invoke(): RevenueSummary {
        val orders = orderRepository.snapshotHistory()
        return RevenueSummary(
            totalPiastres = orders.sumOf { it.grandTotal.amount },
            orderCount = orders.size,
        )
    }
}

class CountPendingSyncOrders(private val orderRepository: OrderRepository) {
    suspend operator fun invoke(): Int {
        return orderRepository.snapshotHistory().count {
            it.status in PENDING_STATUSES
        }
    }
    private val PENDING_STATUSES = setOf(
        OrderStatus.PAID,
        OrderStatus.SYNC_PENDING,
        OrderStatus.SYNC_FAILED,
    )
}

class RankItemsByVolume(private val orderRepository: OrderRepository) {
    /**
     * Returns (productName, totalQuantitySold) pairs. [ascending] = true
     * for slowest movers, false for best sellers. [take] limits the list.
     */
    suspend operator fun invoke(ascending: Boolean, take: Int): List<Pair<String, Int>> {
        val orders = orderRepository.snapshotHistory()
        val tally = mutableMapOf<String, Int>()
        orders.forEach { order ->
            orderRepository.getItems(order.id).forEach { item ->
                tally[item.productName] = (tally[item.productName] ?: 0) + item.quantity
            }
        }
        val sorted = tally.entries.sortedBy { it.value }
        val ordered = if (ascending) sorted else sorted.reversed()
        return ordered.take(take).map { it.key to it.value }
    }
}

/**
 * Fuzzy catalog search. Splits the query into lowercase tokens and ranks
 * each product by token-match score across name, category, and
 * description (with name matches weighted heaviest). Used by the AI
 * assistant to answer "find me a burger" / "do you have pizza?" /
 * "show me cold drinks".
 */
class SearchCatalog(private val productRepository: ProductRepository) {
    suspend operator fun invoke(query: String, take: Int = 5): List<Product> {
        val tokens = query.lowercase()
            .split(' ', ',', '?', '!', '.', '-', '\n', '\t')
            .filter { it.length > 1 }
        if (tokens.isEmpty()) return emptyList()

        val all = productRepository.observeAll().first()
        return all
            .map { it to score(it, tokens) }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .take(take)
            .map { it.first }
    }

    private fun score(product: Product, tokens: List<String>): Int {
        val name = product.name.lowercase()
        val category = product.category.lowercase()
        val description = product.description.lowercase()
        val nameAr = (product.nameAr ?: "").lowercase()
        val categoryAr = (product.categoryAr ?: "").lowercase()
        var total = 0
        for (token in tokens) {
            if (name.contains(token) || nameAr.contains(token)) total += 5
            if (category.contains(token) || categoryAr.contains(token)) total += 3
            if (description.contains(token)) total += 1
        }
        return total
    }
}

/**
 * Recommendation engine for "what should I order?" / "give me the best".
 *
 * Strategy (cheap, deterministic, no model needed):
 *  1. Take the best-selling products by historical volume.
 *  2. If sales data is empty (fresh device), fall back to the highest-
 *     priced available items as a "premium picks" default.
 *  3. Optionally filter by category — "best burger?" only ranks burgers.
 *
 * Returns the actual [Product] entities so the caller can show prices,
 * categories, and add them to the cart.
 */
class RecommendProducts(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
) {
    suspend operator fun invoke(category: String? = null, take: Int = 3): List<Product> {
        val catalog = productRepository.observeAll().first()
            .filter { it.isAvailable }
            .filter { category == null || it.category.equals(category, ignoreCase = true) }
        if (catalog.isEmpty()) return emptyList()

        // 1. Try sales-data ranking
        val rankedNames = run {
            val orders = orderRepository.snapshotHistory()
            val tally = mutableMapOf<String, Int>()
            orders.forEach { order ->
                orderRepository.getItems(order.id).forEach { item ->
                    tally[item.productName] = (tally[item.productName] ?: 0) + item.quantity
                }
            }
            tally.entries.sortedByDescending { it.value }.map { it.key }
        }
        val byVolume = rankedNames
            .mapNotNull { name -> catalog.firstOrNull { it.name == name } }

        // 2. Fill remaining slots from premium fallback (highest price first)
        val fallback = catalog.sortedByDescending { it.price.amount }
        return (byVolume + fallback).distinctBy { it.id }.take(take)
    }
}

/**
 * Resolves a free-text product name (e.g. "double stack" → the Double
 * Stack burger) using the same fuzzy [SearchCatalog]. Returns null if
 * nothing matched confidently, otherwise the top hit. The AI store
 * uses this for "add a double stack to my cart" intents.
 */
class FindProductByName(private val searchCatalog: SearchCatalog) {
    suspend operator fun invoke(query: String): Product? =
        searchCatalog(query, take = 1).firstOrNull()
}
