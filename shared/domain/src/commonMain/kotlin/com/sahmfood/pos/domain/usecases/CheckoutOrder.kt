package com.sahmfood.pos.domain.usecases

import com.sahmfood.pos.domain.common.AppError
import com.sahmfood.pos.domain.common.AppResult
import com.sahmfood.pos.domain.common.appResultOf
import com.sahmfood.pos.domain.entities.CartItem
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderItem
import com.sahmfood.pos.domain.entities.OrderStatus
import com.sahmfood.pos.domain.entities.OrderTotals
import com.sahmfood.pos.domain.entities.PaymentMethod
import com.sahmfood.pos.domain.entities.SyncOpType
import com.sahmfood.pos.domain.entities.SyncQueueEntry
import com.sahmfood.pos.domain.repositories.OrderRepository
import com.sahmfood.pos.domain.repositories.SyncQueueRepository
import com.sahmfood.pos.domain.services.AppClock
import com.sahmfood.pos.domain.services.IdGenerator
import kotlinx.serialization.json.Json

/**
 * Saves the order + items + sync queue entry in one logical operation.
 * Returns an [AppResult]:
 *   Success(order) on persistence success
 *   Failure(Validation) on empty cart / insufficient cash
 *   Failure(Database/Unknown) on a thrown exception inside the repos
 *
 * Stores match on the result instead of wrapping the call in try/catch.
 */
class CheckoutOrder(
    private val orderRepository: OrderRepository,
    private val syncQueueRepository: SyncQueueRepository,
    private val idGenerator: IdGenerator,
    private val clock: AppClock,
    private val json: Json = Json { ignoreUnknownKeys = true },
) {
    suspend operator fun invoke(
        items: List<CartItem>,
        totals: OrderTotals,
        paymentMethod: PaymentMethod,
        tendered: Money,
    ): AppResult<Order> {
        if (items.isEmpty()) {
            return AppResult.failure(AppError.Validation("Cannot checkout an empty cart"))
        }
        if (paymentMethod == PaymentMethod.CASH && tendered < totals.grandTotal) {
            return AppResult.failure(
                AppError.Validation("Tendered amount must cover the grand total")
            )
        }

        return appResultOf {
            val now = clock.nowMillis()
            val orderId = idGenerator.newId()
            val change = if (paymentMethod == PaymentMethod.CASH) {
                tendered - totals.grandTotal
            } else {
                Money.ZERO_EGP
            }
            val order = Order(
                id = orderId,
                subtotal = totals.subtotal,
                tax = totals.taxAmount,
                discount = totals.discount,
                grandTotal = totals.grandTotal,
                status = OrderStatus.PAID,
                paymentMethod = paymentMethod,
                tendered = tendered,
                change = change,
                createdAt = now,
                updatedAt = now,
            )
            val orderItems = items.map { ci ->
                OrderItem(
                    id = idGenerator.newId(),
                    orderId = orderId,
                    productId = ci.product.id,
                    productName = ci.product.name,
                    quantity = ci.quantity,
                    unitPrice = ci.unitPrice,
                    lineTotal = ci.lineTotal,
                )
            }

            orderRepository.save(order, orderItems)

            val payload = json.encodeToString(Order.serializer(), order)
            syncQueueRepository.enqueue(
                SyncQueueEntry(
                    id = idGenerator.newId(),
                    opType = SyncOpType.CREATE_ORDER,
                    orderId = orderId,
                    payloadJson = payload,
                    createdAt = now,
                )
            )
            order
        }
    }
}
