package com.sahmfood.pos.data.repositories

import com.sahmfood.pos.data.db.dao.CartDao
import com.sahmfood.pos.data.db.entities.CartItemEntity
import com.sahmfood.pos.data.mappers.CartItemMapper
import com.sahmfood.pos.domain.entities.PersistedCartLine
import com.sahmfood.pos.domain.repositories.CartRepository
import com.sahmfood.pos.domain.services.AppClock
import com.sahmfood.pos.domain.services.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CartRepositoryImpl(
    private val dao: CartDao,
    private val clock: AppClock,
    private val dispatchers: DispatcherProvider,
) : CartRepository {

    override fun observe(): Flow<List<PersistedCartLine>> =
        dao.observeAll()
            .map(CartItemMapper::toDomainList)
            .flowOn(dispatchers.io)

    override suspend fun snapshot(): List<PersistedCartLine> = withContext(dispatchers.io) {
        CartItemMapper.toDomainList(dao.snapshot())
    }

    override suspend fun setQuantity(productId: String, quantity: Int) = withContext(dispatchers.io) {
        if (quantity <= 0) {
            dao.remove(productId)
        } else {
            val existing = dao.snapshot().firstOrNull { it.productId == productId }
            dao.upsert(
                CartItemEntity(
                    productId = productId,
                    quantity = quantity,
                    addedAt = existing?.addedAt ?: clock.nowMillis(),
                )
            )
        }
    }

    override suspend fun remove(productId: String) = withContext(dispatchers.io) {
        dao.remove(productId)
    }

    override suspend fun clear() = withContext(dispatchers.io) {
        dao.clear()
    }
}
