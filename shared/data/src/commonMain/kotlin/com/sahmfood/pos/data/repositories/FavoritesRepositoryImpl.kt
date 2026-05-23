package com.sahmfood.pos.data.repositories

import com.sahmfood.pos.data.db.dao.FavoriteDao
import com.sahmfood.pos.data.mappers.FavoriteMapper
import com.sahmfood.pos.domain.repositories.FavoritesRepository
import com.sahmfood.pos.domain.services.AppClock
import com.sahmfood.pos.domain.services.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavoritesRepositoryImpl(
    private val dao: FavoriteDao,
    private val clock: AppClock,
    private val dispatchers: DispatcherProvider,
) : FavoritesRepository {

    override fun observeIds(): Flow<Set<String>> =
        dao.observeIds()
            .map { ids -> ids.toSet() }
            .flowOn(dispatchers.io)

    override suspend fun isFavorite(productId: String): Boolean = withContext(dispatchers.io) {
        dao.isFavorite(productId)
    }

    override suspend fun toggle(productId: String): Boolean = withContext(dispatchers.io) {
        if (dao.isFavorite(productId)) {
            dao.remove(productId)
            false
        } else {
            dao.add(FavoriteMapper.fromProductId(productId, clock.nowMillis()))
            true
        }
    }

    override suspend fun add(productId: String) = withContext(dispatchers.io) {
        dao.add(FavoriteMapper.fromProductId(productId, clock.nowMillis()))
    }

    override suspend fun remove(productId: String) = withContext(dispatchers.io) {
        dao.remove(productId)
    }

    override suspend fun clear() = withContext(dispatchers.io) {
        dao.clear()
    }
}
