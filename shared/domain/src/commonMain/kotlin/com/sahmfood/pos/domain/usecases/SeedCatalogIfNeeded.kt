package com.sahmfood.pos.domain.usecases

import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.domain.repositories.ProductRepository

/**
 * One-shot use case the app runs at startup to ensure the catalog has
 * its seed menu. Replaces the previous data-layer CatalogSeed which was
 * directly invoked from the UI layer.
 */
class SeedCatalogIfNeeded(private val productRepository: ProductRepository) {
    suspend operator fun invoke(seedProducts: List<Product>) {
        // Always upsert — Room's onConflict=REPLACE makes this idempotent.
        productRepository.upsertAll(seedProducts)
    }
}
