package com.sahmfood.pos.data.repositories

import com.sahmfood.pos.data.db.dao.OrderDao
import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderItem
import com.sahmfood.pos.domain.entities.OrderStatus
import com.sahmfood.pos.domain.repositories.OrderRepository
import com.sahmfood.pos.domain.services.AppClock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OrderRepositoryImpl(
    private val dao: OrderDao,
    private val clock: AppClock,
) : OrderRepository {

    override suspend fun save(order: Order, items: List<OrderItem>) {
        dao.saveOrderWithItems(
            order = order.toEntity(),
            items = items.map { it.toEntity() },
        )
    }

    override suspend fun updateStatus(orderId: String, status: OrderStatus) {
        dao.updateStatus(orderId, status.name, clock.nowMillis())
    }

    override fun observeHistory(): Flow<List<Order>> =
        dao.observeHistory().map { list -> list.map { it.toDomain() } }

    override suspend fun snapshotHistory(): List<Order> =
        dao.getHistorySnapshot().map { it.toDomain() }

    override suspend fun getById(orderId: String): Order? =
        dao.getById(orderId)?.toDomain()

    override suspend fun getItems(orderId: String): List<OrderItem> =
        dao.getItems(orderId).map { it.toDomain() }
}
