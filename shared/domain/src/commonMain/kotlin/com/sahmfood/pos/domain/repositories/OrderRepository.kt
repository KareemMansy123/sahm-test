package com.sahmfood.pos.domain.repositories

import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderItem
import com.sahmfood.pos.domain.entities.OrderStatus
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun save(order: Order, items: List<OrderItem>)
    suspend fun updateStatus(orderId: String, status: OrderStatus)
    fun observeHistory(): Flow<List<Order>>
    suspend fun snapshotHistory(): List<Order>
    suspend fun getById(orderId: String): Order?
    suspend fun getItems(orderId: String): List<OrderItem>
}
