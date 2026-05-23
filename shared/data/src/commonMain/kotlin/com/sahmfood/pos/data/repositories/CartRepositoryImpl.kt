package com.sahmfood.pos.data.repositories

import com.sahmfood.pos.data.db.dao.CartDao
import com.sahmfood.pos.data.db.entities.CartItemEntity
import com.sahmfood.pos.domain.entities.PersistedCartLine
import com.sahmfood.pos.domain.repositories.CartRepository
import com.sahmfood.pos.domain.services.AppClock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepositoryImpl(
    private val dao: CartDao,
    private val clock: AppClock,
) : CartRepository {

    override fun observe(): Flow<List<PersistedCartLine>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun snapshot(): List<PersistedCartLine> =
        dao.snapshot().map { it.toDomain() }

    override suspend fun setQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            dao.remove(productId)
        } else {
            // upsert with addedAt preserved if exists, else now
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

    override suspend fun remove(productId: String) {
        dao.remove(productId)
    }

    override suspend fun clear() {
        dao.clear()
    }
}
