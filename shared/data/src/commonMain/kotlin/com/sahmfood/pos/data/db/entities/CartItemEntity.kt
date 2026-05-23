package com.sahmfood.pos.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One row per product currently in the draft cart. Primary key is the
 * productId so increment/decrement is a single upsert.
 */
@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val quantity: Int,
    val addedAt: Long,
)
