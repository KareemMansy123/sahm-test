package com.sahmfood.pos.domain.entities

import kotlinx.serialization.Serializable

enum class OrderStatus { DRAFT, PAID, SYNC_PENDING, SYNCED, SYNC_FAILED }

enum class PaymentMethod { CASH, CARD }

@Serializable
data class Order(
    val id: String,
    val subtotal: Money,
    val tax: Money,
    val discount: Money,
    val grandTotal: Money,
    val status: OrderStatus,
    val paymentMethod: PaymentMethod,
    val tendered: Money,
    val change: Money,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class OrderItem(
    val id: String,
    val orderId: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Money,
    val lineTotal: Money
)
