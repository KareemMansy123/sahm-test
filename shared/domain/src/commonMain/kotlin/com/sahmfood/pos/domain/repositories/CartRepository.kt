package com.sahmfood.pos.domain.repositories

import com.sahmfood.pos.domain.entities.PersistedCartLine
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun observe(): Flow<List<PersistedCartLine>>
    suspend fun snapshot(): List<PersistedCartLine>
    suspend fun setQuantity(productId: String, quantity: Int)
    suspend fun remove(productId: String)
    suspend fun clear()
}
