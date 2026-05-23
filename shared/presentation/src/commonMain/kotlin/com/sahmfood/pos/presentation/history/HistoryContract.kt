package com.sahmfood.pos.presentation.history

import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderItem

data class HistoryState(
    val orders: List<Order> = emptyList(),
    val selectedOrderId: String? = null,
    val selectedOrderItems: List<OrderItem> = emptyList(),
    val isLoading: Boolean = true
) {
    val selectedOrder: Order? get() = orders.firstOrNull { it.id == selectedOrderId }
    val todayOrderCount: Int get() = orders.size
    val todayRevenue: Long get() = orders.sumOf { it.grandTotal.amount }
}

sealed interface HistoryIntent {
    data object Load : HistoryIntent
    data class SelectOrder(val orderId: String) : HistoryIntent
    data object ClearSelection : HistoryIntent
}

sealed interface HistoryEffect {
    data class ShowError(val message: String) : HistoryEffect
}
