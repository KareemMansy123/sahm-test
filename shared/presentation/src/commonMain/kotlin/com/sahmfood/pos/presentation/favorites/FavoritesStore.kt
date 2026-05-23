package com.sahmfood.pos.presentation.favorites

import com.sahmfood.pos.domain.usecases.GetFavoriteProducts
import com.sahmfood.pos.domain.usecases.ObserveFavoriteIds
import com.sahmfood.pos.domain.usecases.ToggleFavorite
import com.sahmfood.pos.presentation.common.BaseStore
import kotlinx.coroutines.launch

class FavoritesStore(
    private val observeFavoriteIds: ObserveFavoriteIds,
    private val getFavoriteProducts: GetFavoriteProducts,
    private val toggleFavorite: ToggleFavorite,
) : BaseStore<FavoritesState, FavoritesIntent, FavoritesEffect>(FavoritesState()) {

    init {
        scope.launch {
            observeFavoriteIds().collect { ids ->
                updateState { it.copy(favoriteIds = ids) }
            }
        }
        scope.launch {
            getFavoriteProducts().collect { products ->
                updateState { it.copy(favoriteProducts = products, isLoading = false) }
            }
        }
    }

    override suspend fun handle(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.Toggle -> {
                val nowFav = toggleFavorite(intent.productId)
                emitEffect(FavoritesEffect.Toggled(intent.productId, nowFav))
            }
            is FavoritesIntent.Remove -> {
                val wasFav = state.value.favoriteIds.contains(intent.productId)
                if (wasFav) {
                    toggleFavorite(intent.productId)
                    emitEffect(FavoritesEffect.Toggled(intent.productId, false))
                }
            }
        }
    }
}
