package com.sahmfood.pos.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val name: String,
    val price: Money,
    val category: String,
    val imageUrl: String?,
    val isAvailable: Boolean = true,
    val description: String = ""
)
