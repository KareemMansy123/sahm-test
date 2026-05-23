package com.sahmfood.pos.data.repositories

import com.sahmfood.pos.data.db.PosOrder as PosOrderRow
import com.sahmfood.pos.data.db.PosOrderItem as PosOrderItemRow
import com.sahmfood.pos.data.db.Product as ProductRow
import com.sahmfood.pos.data.db.SyncQueueEntry as SyncRow
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderItem
import com.sahmfood.pos.domain.entities.OrderStatus
import com.sahmfood.pos.domain.entities.PaymentMethod
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.domain.entities.SyncOpType
import com.sahmfood.pos.domain.entities.SyncQueueEntry
import com.sahmfood.pos.domain.entities.SyncStatus

internal fun ProductRow.toDomain(): Product = Product(
    id = id,
    name = name,
    price = Money(price_amount, currency),
    category = category,
    imageUrl = image_url,
    description = description,
    isAvailable = is_available == 1L
)

internal fun PosOrderRow.toDomain(): Order = Order(
    id = id,
    subtotal = Money(subtotal, currency),
    tax = Money(tax_amount, currency),
    discount = Money(discount, currency),
    grandTotal = Money(grand_total, currency),
    status = OrderStatus.valueOf(status),
    paymentMethod = PaymentMethod.valueOf(payment_method),
    tendered = Money(tendered, currency),
    change = Money(change_amount, currency),
    createdAt = created_at,
    updatedAt = updated_at
)

internal fun PosOrderItemRow.toDomain(): OrderItem = OrderItem(
    id = id,
    orderId = order_id,
    productId = product_id,
    productName = product_name,
    quantity = quantity.toInt(),
    unitPrice = Money(unit_price, currency),
    lineTotal = Money(line_total, currency)
)

internal fun SyncRow.toDomain(): SyncQueueEntry = SyncQueueEntry(
    id = id,
    opType = SyncOpType.valueOf(op_type),
    orderId = order_id,
    payloadJson = payload_json,
    attempts = attempts.toInt(),
    status = SyncStatus.valueOf(status),
    createdAt = created_at
)
