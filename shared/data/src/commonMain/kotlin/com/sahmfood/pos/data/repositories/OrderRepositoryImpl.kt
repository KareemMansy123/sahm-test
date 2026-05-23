package com.sahmfood.pos.data.repositories

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.sahmfood.pos.data.db.SahmPosDatabase
import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderItem
import com.sahmfood.pos.domain.entities.OrderStatus
import com.sahmfood.pos.domain.repositories.OrderRepository
import com.sahmfood.pos.domain.services.AppClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OrderRepositoryImpl(
    private val db: SahmPosDatabase,
    private val clock: AppClock
) : OrderRepository {

    override suspend fun save(order: Order, items: List<OrderItem>) = withContext(Dispatchers.IO) {
        db.transaction {
            db.sahmPosDatabaseQueries.insertOrder(
                id = order.id,
                subtotal = order.subtotal.amount,
                tax_amount = order.tax.amount,
                discount = order.discount.amount,
                grand_total = order.grandTotal.amount,
                currency = order.subtotal.currency,
                status = order.status.name,
                payment_method = order.paymentMethod.name,
                tendered = order.tendered.amount,
                change_amount = order.change.amount,
                created_at = order.createdAt,
                updated_at = order.updatedAt
            )
            items.forEach { item ->
                db.sahmPosDatabaseQueries.insertOrderItem(
                    id = item.id,
                    order_id = item.orderId,
                    product_id = item.productId,
                    product_name = item.productName,
                    quantity = item.quantity.toLong(),
                    unit_price = item.unitPrice.amount,
                    line_total = item.lineTotal.amount,
                    currency = item.unitPrice.currency
                )
            }
        }
    }

    override suspend fun updateStatus(orderId: String, status: OrderStatus) = withContext(Dispatchers.IO) {
        db.sahmPosDatabaseQueries.updateOrderStatus(
            status = status.name,
            updated_at = clock.nowMillis(),
            id = orderId
        )
    }

    override fun observeHistory(): Flow<List<Order>> =
        db.sahmPosDatabaseQueries.selectOrderHistory()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)

    override suspend fun getById(orderId: String): Order? = withContext(Dispatchers.IO) {
        db.sahmPosDatabaseQueries.selectOrderById(orderId).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun getItems(orderId: String): List<OrderItem> = withContext(Dispatchers.IO) {
        db.sahmPosDatabaseQueries.selectItemsByOrderId(orderId).executeAsList().map { it.toDomain() }
    }
}
