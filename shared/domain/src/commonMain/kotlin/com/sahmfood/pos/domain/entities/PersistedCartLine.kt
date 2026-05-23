package com.sahmfood.pos.domain.entities

/**
 * The persistable shape of a cart row. The presentation layer's
 * [CartItem] joins this with the live [Product] from the catalog so that
 * price/name changes propagate without rewriting the cart on every menu
 * update.
 */
data class PersistedCartLine(
    val productId: String,
    val quantity: Int,
    val addedAt: Long,
)
