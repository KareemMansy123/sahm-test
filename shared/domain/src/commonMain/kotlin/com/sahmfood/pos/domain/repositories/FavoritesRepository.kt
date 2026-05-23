package com.sahmfood.pos.domain.repositories

import kotlinx.coroutines.flow.Flow

/**
 * Per-cashier favorite-product list. Persisted locally so the cashier can
 * pin frequently-sold items for one-tap add at the start of a shift.
 */
interface FavoritesRepository {
    fun observeIds(): Flow<Set<String>>
    suspend fun isFavorite(productId: String): Boolean
    suspend fun toggle(productId: String): Boolean
    suspend fun add(productId: String)
    suspend fun remove(productId: String)
    suspend fun clear()
}
