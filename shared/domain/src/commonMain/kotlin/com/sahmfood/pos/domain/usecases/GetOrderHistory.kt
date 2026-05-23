package com.sahmfood.pos.domain.usecases

import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.repositories.OrderRepository
import kotlinx.coroutines.flow.Flow

class GetOrderHistory(private val orderRepository: OrderRepository) {
    operator fun invoke(): Flow<List<Order>> = orderRepository.observeHistory()
}

class GetOrderDetails(private val orderRepository: OrderRepository) {
    suspend operator fun invoke(orderId: String): Pair<Order, List<com.sahmfood.pos.domain.entities.OrderItem>>? {
        val order = orderRepository.getById(orderId) ?: return null
        val items = orderRepository.getItems(orderId)
        return order to items
    }
}
