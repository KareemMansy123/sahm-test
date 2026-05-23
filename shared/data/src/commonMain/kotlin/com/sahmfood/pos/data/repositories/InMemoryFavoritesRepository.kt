package com.sahmfood.pos.data.repositories

import com.sahmfood.pos.domain.repositories.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory favorites store. Persists for the app process lifetime;
 * survives screen navigation but not process death. A future iteration
 * would back this with a SQLDelight `FavoriteEntry` table — the
 * interface is stable so the swap requires no presentation changes.
 */
class InMemoryFavoritesRepository : FavoritesRepository {
    private val state = MutableStateFlow<Set<String>>(emptySet())

    override fun observeIds(): Flow<Set<String>> = state.asStateFlow()

    override suspend fun isFavorite(productId: String): Boolean =
        productId in state.value

    override suspend fun toggle(productId: String): Boolean {
        val current = state.value
        val now = if (productId in current) current - productId else current + productId
        state.value = now
        return productId in now
    }

    override suspend fun add(productId: String) {
        state.value = state.value + productId
    }

    override suspend fun remove(productId: String) {
        state.value = state.value - productId
    }

    override suspend fun clear() {
        state.value = emptySet()
    }
}
