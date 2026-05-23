package com.sahmfood.pos.domain.usecases

import com.sahmfood.pos.domain.entities.CartItem
import com.sahmfood.pos.domain.entities.PersistedCartLine
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.domain.repositories.CartRepository
import com.sahmfood.pos.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Reads the persisted cart, joins it with live product data, and emits a
 * [List<CartItem>] for the presentation layer. Prices/names always
 * reflect the current catalog — if a product's price changes, the cart
 * total updates automatically on next emission.
 */
class ObserveCart(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) {
    operator fun invoke(): Flow<List<CartItem>> =
        combine(cartRepository.observe(), productRepository.observeAll()) { lines, products ->
            lines.toCartItems(products)
        }.distinctUntilChanged()

    private fun List<PersistedCartLine>.toCartItems(products: List<Product>): List<CartItem> {
        val byId = products.associateBy { it.id }
        return mapNotNull { line ->
            val product = byId[line.productId] ?: return@mapNotNull null
            CartItem(product = product, quantity = line.quantity)
        }
    }
}

class AddProductToCart(private val cartRepository: CartRepository) {
    suspend operator fun invoke(productId: String, currentLines: List<PersistedCartLine>) {
        val existing = currentLines.firstOrNull { it.productId == productId }
        val newQty = (existing?.quantity ?: 0) + 1
        cartRepository.setQuantity(productId, newQty)
    }
}

class SetCartItemQuantity(private val cartRepository: CartRepository) {
    suspend operator fun invoke(productId: String, quantity: Int) {
        cartRepository.setQuantity(productId, quantity)
    }
}

class RemoveCartItem(private val cartRepository: CartRepository) {
    suspend operator fun invoke(productId: String) {
        cartRepository.remove(productId)
    }
}

class ClearCart(private val cartRepository: CartRepository) {
    suspend operator fun invoke() {
        cartRepository.clear()
    }
}

class SnapshotCart(private val cartRepository: CartRepository) {
    suspend operator fun invoke(): List<PersistedCartLine> = cartRepository.snapshot()
}
