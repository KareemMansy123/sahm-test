package com.sahmfood.pos.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val priceAmount: Long,
    val currency: String,
    val category: String,
    val imageUrl: String?,
    val description: String,
    val isAvailable: Boolean,
)
