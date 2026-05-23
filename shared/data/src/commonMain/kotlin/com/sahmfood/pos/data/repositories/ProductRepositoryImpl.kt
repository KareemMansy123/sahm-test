package com.sahmfood.pos.data.repositories

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.sahmfood.pos.data.db.SahmPosDatabase
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.domain.repositories.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ProductRepositoryImpl(
    private val db: SahmPosDatabase
) : ProductRepository {

    override fun observeAll(): Flow<List<Product>> =
        db.sahmPosDatabaseQueries.selectAllProducts()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)

    override suspend fun getById(id: String): Product? = withContext(Dispatchers.IO) {
        db.sahmPosDatabaseQueries.selectProductById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun upsertAll(products: List<Product>) = withContext(Dispatchers.IO) {
        db.transaction {
            products.forEach { p ->
                db.sahmPosDatabaseQueries.upsertProduct(
                    id = p.id,
                    name = p.name,
                    price_amount = p.price.amount,
                    currency = p.price.currency,
                    category = p.category,
                    image_url = p.imageUrl,
                    description = p.description,
                    is_available = if (p.isAvailable) 1L else 0L
                )
            }
        }
    }
}
