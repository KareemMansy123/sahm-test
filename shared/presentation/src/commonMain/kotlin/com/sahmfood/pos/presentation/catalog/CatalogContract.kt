package com.sahmfood.pos.presentation.catalog

import com.sahmfood.pos.domain.entities.CartItem
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.OrderTotals
import com.sahmfood.pos.domain.entities.Product

data class CatalogState(
    val products: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val cart: List<CartItem> = emptyList(),
    val totals: OrderTotals = OrderTotals.EMPTY,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val filteredProducts: List<Product> get() {
        val q = searchQuery.trim().lowercase()
        return products
            .filter { selectedCategory == null || it.category == selectedCategory }
            .filter { q.isEmpty() || it.name.lowercase().contains(q) }
    }
    val cartItemCount: Int get() = cart.sumOf { it.quantity }
    val isCartEmpty: Boolean get() = cart.isEmpty()
}

sealed interface CatalogIntent {
    data object LoadCatalog : CatalogIntent
    data class SetSearchQuery(val query: String) : CatalogIntent
    data class SelectCategory(val category: String?) : CatalogIntent
    data class AddToCart(val product: Product) : CatalogIntent
    data class RemoveFromCart(val productId: String) : CatalogIntent
    data class UpdateQuantity(val productId: String, val quantity: Int) : CatalogIntent
    data object ClearCart : CatalogIntent
    data object Checkout : CatalogIntent
}

sealed interface CatalogEffect {
    data class ProductAdded(val product: Product) : CatalogEffect
    data class NavigateToCheckout(val cart: List<CartItem>, val totals: OrderTotals) : CatalogEffect
    data class ShowError(val message: String) : CatalogEffect
}
