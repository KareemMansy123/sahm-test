package com.sahmfood.pos.domain.usecases

import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetProductCatalog(private val productRepository: ProductRepository) {
    operator fun invoke(): Flow<List<Product>> = productRepository.observeAll()
}
