package com.sahmfood.pos.domain.usecases

import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.domain.repositories.FavoritesRepository
import com.sahmfood.pos.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveFavoriteIds(private val repo: FavoritesRepository) {
    operator fun invoke(): Flow<Set<String>> = repo.observeIds()
}

class ToggleFavorite(private val repo: FavoritesRepository) {
    suspend operator fun invoke(productId: String): Boolean = repo.toggle(productId)
}

class GetFavoriteProducts(
    private val favorites: FavoritesRepository,
    private val products: ProductRepository,
) {
    /** Joins the favorite id set with the product catalog, dropping deleted items. */
    operator fun invoke(): Flow<List<Product>> =
        combine(favorites.observeIds(), products.observeAll()) { ids, all ->
            all.filter { it.id in ids }
        }
}
