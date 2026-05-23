package com.sahmfood.pos.data.repositories

import com.sahmfood.pos.data.db.dao.ProductDao
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(private val dao: ProductDao) : ProductRepository {

    override fun observeAll(): Flow<List<Product>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): Product? =
        dao.getById(id)?.toDomain()

    override suspend fun upsertAll(products: List<Product>) {
        dao.upsertAll(products.map { it.toEntity() })
    }
}
