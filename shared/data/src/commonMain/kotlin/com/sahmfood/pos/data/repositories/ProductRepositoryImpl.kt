package com.sahmfood.pos.data.repositories

import com.sahmfood.pos.data.db.dao.ProductDao
import com.sahmfood.pos.data.mappers.ProductMapper
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.domain.repositories.ProductRepository
import com.sahmfood.pos.domain.services.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ProductRepositoryImpl(
    private val dao: ProductDao,
    private val dispatchers: DispatcherProvider,
) : ProductRepository {

    override fun observeAll(): Flow<List<Product>> =
        dao.observeAll()
            .map(ProductMapper::toDomainList)
            .flowOn(dispatchers.io)

    override suspend fun getById(id: String): Product? = withContext(dispatchers.io) {
        dao.getById(id)?.let(ProductMapper::toDomain)
    }

    override suspend fun upsertAll(products: List<Product>) = withContext(dispatchers.io) {
        dao.upsertAll(ProductMapper.toEntityList(products))
    }
}
