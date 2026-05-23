package com.sahmfood.pos.domain.entities

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    init {
        require(quantity > 0) { "cart item quantity must be positive (got $quantity)" }
    }

    val unitPrice: Money get() = product.price
    val lineTotal: Money get() = product.price * quantity
}
