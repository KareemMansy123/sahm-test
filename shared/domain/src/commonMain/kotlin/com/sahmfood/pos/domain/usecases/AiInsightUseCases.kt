package com.sahmfood.pos.domain.usecases

import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderStatus
import com.sahmfood.pos.domain.repositories.OrderRepository
import kotlinx.coroutines.flow.Flow

/**
 * Read-only insights the AI assistant exposes. Each lives as a single-
 * purpose use case so the store calls a verb, not a repository.
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
