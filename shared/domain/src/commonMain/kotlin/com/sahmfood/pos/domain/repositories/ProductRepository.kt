package com.sahmfood.pos.domain.repositories

import com.sahmfood.pos.domain.entities.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeAll(): Flow<List<Product>>
    suspend fun getById(id: String): Product?
    suspend fun upsertAll(products: List<Product>)
}
