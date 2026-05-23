package com.sahmfood.pos.data.mappers

import com.sahmfood.pos.data.db.entities.OrderEntity
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderStatus
import com.sahmfood.pos.domain.entities.PaymentMethod

object OrderMapper {
    fun toDomain(entity: OrderEntity): Order = Order(
        id = entity.id,
        subtotal = Money(entity.subtotal, entity.currency),
        tax = Money(entity.taxAmount, entity.currency),
        discount = Money(entity.discount, entity.currency),
        grandTotal = Money(entity.grandTotal, entity.currency),
        status = OrderStatus.valueOf(entity.status),
        paymentMethod = PaymentMethod.valueOf(entity.paymentMethod),
        tendered = Money(entity.tendered, entity.currency),
        change = Money(entity.changeAmount, entity.currency),
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
    )

    fun toEntity(order: Order): OrderEntity = OrderEntity(
        id = order.id,
        subtotal = order.subtotal.amount,
        taxAmount = order.tax.amount,
        discount = order.discount.amount,
        grandTotal = order.grandTotal.amount,
        currency = order.subtotal.currency,
        status = order.status.name,
        paymentMethod = order.paymentMethod.name,
        tendered = order.tendered.amount,
        changeAmount = order.change.amount,
        createdAt = order.createdAt,
        updatedAt = order.updatedAt,
    )

    fun toDomainList(entities: List<OrderEntity>): List<Order> = entities.map(::toDomain)
}
