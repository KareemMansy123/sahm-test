package com.sahmfood.pos.domain.usecases

import com.sahmfood.pos.domain.entities.CartItem
import com.sahmfood.pos.domain.entities.Product

/**
 * Pure cart mutation use cases. They live as classes (not free functions) so
 * the DI container can resolve them uniformly with the orchestrating use cases
 * and so future variants (e.g. AddItemToCart that consults a stock service)
 * can be substituted without changing call sites.
 */

class AddItemToCart {
    operator fun invoke(current: List<CartItem>, product: Product): List<CartItem> {
        val existing = current.firstOrNull { it.product.id == product.id }
        return if (existing != null) {
            current.map {
                if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
            }
        } else {
            current + CartItem(product, quantity = 1)
        }
    }
}

class RemoveItemFromCart {
    operator fun invoke(current: List<CartItem>, productId: String): List<CartItem> =
        current.filterNot { it.product.id == productId }
}

class UpdateItemQuantity {
    operator fun invoke(
        current: List<CartItem>,
        productId: String,
        quantity: Int
    ): List<CartItem> {
        if (quantity <= 0) {
            return current.filterNot { it.product.id == productId }
        }
        return current.map {
            if (it.product.id == productId) it.copy(quantity = quantity) else it
        }
    }
}
