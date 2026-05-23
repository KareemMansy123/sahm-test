package com.sahmfood.pos.data.repositories

import com.sahmfood.pos.data.db.dao.FavoriteDao
import com.sahmfood.pos.data.db.entities.FavoriteEntity
import com.sahmfood.pos.domain.repositories.FavoritesRepository
import com.sahmfood.pos.domain.services.AppClock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val dao: FavoriteDao,
    private val clock: AppClock,
) : FavoritesRepository {

    override fun observeIds(): Flow<Set<String>> =
        dao.observeIds().map { it.toSet() }

    override suspend fun isFavorite(productId: String): Boolean =
        dao.isFavorite(productId)

    override suspend fun toggle(productId: String): Boolean {
        return if (dao.isFavorite(productId)) {
            dao.remove(productId)
            false
        } else {
            dao.add(FavoriteEntity(productId = productId, addedAt = clock.nowMillis()))
            true
        }
    }

    override suspend fun add(productId: String) {
        dao.add(FavoriteEntity(productId = productId, addedAt = clock.nowMillis()))
    }

    override suspend fun remove(productId: String) {
        dao.remove(productId)
    }

    override suspend fun clear() {
        dao.clear()
    }
}
