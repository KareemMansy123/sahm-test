package com.sahmfood.pos.data.mappers

import com.sahmfood.pos.data.db.entities.OrderItemEntity
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.OrderItem

object OrderItemMapper {
    fun toDomain(entity: OrderItemEntity): OrderItem = OrderItem(
        id = entity.id,
        orderId = entity.orderId,
        productId = entity.productId,
        productName = entity.productName,
        quantity = entity.quantity,
        unitPrice = Money(entity.unitPrice, entity.currency),
        lineTotal = Money(entity.lineTotal, entity.currency),
    )

    fun toEntity(item: OrderItem): OrderItemEntity = OrderItemEntity(
        id = item.id,
        orderId = item.orderId,
        productId = item.productId,
        productName = item.productName,
        quantity = item.quantity,
        unitPrice = item.unitPrice.amount,
        lineTotal = item.lineTotal.amount,
        currency = item.unitPrice.currency,
    )

    fun toDomainList(entities: List<OrderItemEntity>): List<OrderItem> = entities.map(::toDomain)
    fun toEntityList(items: List<OrderItem>): List<OrderItemEntity> = items.map(::toEntity)
}
