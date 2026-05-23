package com.sahmfood.pos.domain.entities

data class Receipt(
    val receiptNumber: String,
    val storeName: String,
    val storeAddress: String,
    val order: Order,
    val items: List<OrderItem>,
    val issuedAt: Long,
    val footerMessage: String = "Thank you! Come again."
)
