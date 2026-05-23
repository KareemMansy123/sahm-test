package com.sahmfood.pos.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val subtotal: Long,
    val taxAmount: Long,
    val discount: Long,
    val grandTotal: Long,
    val currency: String,
    val status: String,
    val paymentMethod: String,
    val tendered: Long,
    val changeAmount: Long,
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("orderId")],
)
data class OrderItemEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Long,
    val lineTotal: Long,
    val currency: String,
)
