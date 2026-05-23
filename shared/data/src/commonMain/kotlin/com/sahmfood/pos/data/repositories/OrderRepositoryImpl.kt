package com.sahmfood.pos.data.repositories

import com.sahmfood.pos.data.db.dao.OrderDao
import com.sahmfood.pos.data.mappers.OrderItemMapper
import com.sahmfood.pos.data.mappers.OrderMapper
import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderItem
import com.sahmfood.pos.domain.entities.OrderStatus
import com.sahmfood.pos.domain.repositories.OrderRepository
import com.sahmfood.pos.domain.services.AppClock
import com.sahmfood.pos.domain.services.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OrderRepositoryImpl(
    private val dao: OrderDao,
    private val clock: AppClock,
    private val dispatchers: DispatcherProvider,
) : OrderRepository {

    override suspend fun save(order: Order, items: List<OrderItem>) = withContext(dispatchers.io) {
        dao.saveOrderWithItems(
            order = OrderMapper.toEntity(order),
            items = OrderItemMapper.toEntityList(items),
        )
    }

    override suspend fun updateStatus(orderId: String, status: OrderStatus) = withContext(dispatchers.io) {
        dao.updateStatus(orderId, status.name, clock.nowMillis())
    }

    override fun observeHistory(): Flow<List<Order>> =
        dao.observeHistory()
            .map(OrderMapper::toDomainList)
            .flowOn(dispatchers.io)

    override suspend fun snapshotHistory(): List<Order> = withContext(dispatchers.io) {
        OrderMapper.toDomainList(dao.getHistorySnapshot())
    }

    override suspend fun getById(orderId: String): Order? = withContext(dispatchers.io) {
        dao.getById(orderId)?.let(OrderMapper::toDomain)
    }

    override suspend fun getItems(orderId: String): List<OrderItem> = withContext(dispatchers.io) {
        OrderItemMapper.toDomainList(dao.getItems(orderId))
    }
}
