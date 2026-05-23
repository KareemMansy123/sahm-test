package com.sahmfood.pos.data.repositories

import com.sahmfood.pos.data.db.entities.CartItemEntity
import com.sahmfood.pos.data.db.entities.ChatMessageEntity
import com.sahmfood.pos.data.db.entities.FavoriteEntity
import com.sahmfood.pos.data.db.entities.OrderEntity
import com.sahmfood.pos.data.db.entities.OrderItemEntity
import com.sahmfood.pos.data.db.entities.ProductEntity
import com.sahmfood.pos.data.db.entities.SyncQueueEntity
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderItem
import com.sahmfood.pos.domain.entities.OrderStatus
import com.sahmfood.pos.domain.entities.PaymentMethod
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.domain.entities.SyncOpType
import com.sahmfood.pos.domain.entities.SyncQueueEntry
import com.sahmfood.pos.domain.entities.SyncStatus

internal fun ProductEntity.toDomain(): Product = Product(
    id = id,
    name = name,
    price = Money(priceAmount, currency),
    category = category,
    imageUrl = imageUrl,
    description = description,
    isAvailable = isAvailable,
)

internal fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    name = name,
    priceAmount = price.amount,
    currency = price.currency,
    category = category,
    imageUrl = imageUrl,
    description = description,
    isAvailable = isAvailable,
)

internal fun OrderEntity.toDomain(): Order = Order(
    id = id,
    subtotal = Money(subtotal, currency),
    tax = Money(taxAmount, currency),
    discount = Money(discount, currency),
    grandTotal = Money(grandTotal, currency),
    status = OrderStatus.valueOf(status),
    paymentMethod = PaymentMethod.valueOf(paymentMethod),
    tendered = Money(tendered, currency),
    change = Money(changeAmount, currency),
    createdAt = createdAt,
    updatedAt = updatedAt,
)

internal fun Order.toEntity(): OrderEntity = OrderEntity(
    id = id,
    subtotal = subtotal.amount,
    taxAmount = tax.amount,
    discount = discount.amount,
    grandTotal = grandTotal.amount,
    currency = subtotal.currency,
    status = status.name,
    paymentMethod = paymentMethod.name,
    tendered = tendered.amount,
    changeAmount = change.amount,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

internal fun OrderItemEntity.toDomain(): OrderItem = OrderItem(
    id = id,
    orderId = orderId,
    productId = productId,
    productName = productName,
    quantity = quantity,
    unitPrice = Money(unitPrice, currency),
    lineTotal = Money(lineTotal, currency),
)

internal fun OrderItem.toEntity(): OrderItemEntity = OrderItemEntity(
    id = id,
    orderId = orderId,
    productId = productId,
    productName = productName,
    quantity = quantity,
    unitPrice = unitPrice.amount,
    lineTotal = lineTotal.amount,
    currency = unitPrice.currency,
)

internal fun SyncQueueEntity.toDomain(): SyncQueueEntry = SyncQueueEntry(
    id = id,
    opType = SyncOpType.valueOf(opType),
    orderId = orderId,
    payloadJson = payloadJson,
    attempts = attempts,
    status = SyncStatus.valueOf(status),
    createdAt = createdAt,
)

internal fun SyncQueueEntry.toEntity(): SyncQueueEntity = SyncQueueEntity(
    id = id,
    opType = opType.name,
    orderId = orderId,
    payloadJson = payloadJson,
    attempts = attempts,
    status = status.name,
    createdAt = createdAt,
)

internal fun FavoriteEntity.toProductId(): String = productId

internal fun ChatMessageEntity.toDomain():
    com.sahmfood.pos.domain.entities.ChatMessage =
    com.sahmfood.pos.domain.entities.ChatMessage(
        id = id,
        role = com.sahmfood.pos.domain.entities.ChatRole.valueOf(role),
        content = content,
        timestampMs = timestampMs,
    )

internal fun com.sahmfood.pos.domain.entities.ChatMessage.toEntity(): ChatMessageEntity =
    ChatMessageEntity(
        id = id,
        role = role.name,
        content = content,
        timestampMs = timestampMs,
    )

internal fun CartItemEntity.toDomain():
    com.sahmfood.pos.domain.entities.PersistedCartLine =
    com.sahmfood.pos.domain.entities.PersistedCartLine(
        productId = productId,
        quantity = quantity,
        addedAt = addedAt,
    )
