package com.sahmfood.pos.presentation.favorites

import com.sahmfood.pos.domain.entities.Product

data class FavoritesState(
    val favoriteIds: Set<String> = emptySet(),
    val favoriteProducts: List<Product> = emptyList(),
    val isLoading: Boolean = true,
)

sealed interface FavoritesIntent {
    data class Toggle(val productId: String) : FavoritesIntent
    data class Remove(val productId: String) : FavoritesIntent
}

sealed interface FavoritesEffect {
    data class Toggled(val productId: String, val isNowFavorite: Boolean) : FavoritesEffect
}
