package com.sahmfood.pos.presentation.catalog

import com.sahmfood.pos.domain.entities.OrderTotals
import com.sahmfood.pos.domain.usecases.AddItemToCart
import com.sahmfood.pos.domain.usecases.CalculateOrderTotals
import com.sahmfood.pos.domain.usecases.GetProductCatalog
import com.sahmfood.pos.domain.usecases.RemoveItemFromCart
import com.sahmfood.pos.domain.usecases.UpdateItemQuantity
import com.sahmfood.pos.presentation.common.BaseStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * CatalogStore owns both the product catalog and the in-progress cart. The
 * architecture spec considered splitting these into separate stores; in
 * practice they are tightly coupled (every add-to-cart starts from a product
 * the catalog rendered) and merging avoids cross-store coordination and the
 * risk of a stale cart projection.
 */
class CatalogStore(
    private val getProductCatalog: GetProductCatalog,
    private val addItemToCart: AddItemToCart,
    private val removeItemFromCart: RemoveItemFromCart,
    private val updateItemQuantity: UpdateItemQuantity,
    private val calculateOrderTotals: CalculateOrderTotals
) : BaseStore<CatalogState, CatalogIntent, CatalogEffect>(CatalogState()) {

    init {
        // Eager catalog load — first emission populates state, subsequent
        // emissions keep it in sync if products change in the DB.
        scope.launch {
            getProductCatalog()
                .catch { t ->
                    updateState { it.copy(isLoading = false, errorMessage = t.message ?: "load failed") }
                }
                .collect { products ->
                    val categories = products.map { it.category }.distinct()
                    updateState {
                        it.copy(
                            products = products,
                            categories = categories,
                            isLoading = false
                        )
                    }
                }
        }
    }

    override suspend fun handle(intent: CatalogIntent) {
        when (intent) {
            CatalogIntent.LoadCatalog -> {
                updateState { it.copy(isLoading = true, errorMessage = null) }
                // Flow is already collected in init — nothing else to do.
            }
            is CatalogIntent.SetSearchQuery -> updateState { it.copy(searchQuery = intent.query) }
            is CatalogIntent.SelectCategory -> updateState { it.copy(selectedCategory = intent.category) }
            is CatalogIntent.AddToCart -> {
                val newCart = addItemToCart(state.value.cart, intent.product)
                val newTotals = calculateOrderTotals(newCart)
                updateState { it.copy(cart = newCart, totals = newTotals) }
                emitEffect(CatalogEffect.ProductAdded(intent.product))
            }
            is CatalogIntent.RemoveFromCart -> {
                val newCart = removeItemFromCart(state.value.cart, intent.productId)
                val newTotals = calculateOrderTotals(newCart)
                updateState { it.copy(cart = newCart, totals = newTotals) }
            }
            is CatalogIntent.UpdateQuantity -> {
                val newCart = updateItemQuantity(state.value.cart, intent.productId, intent.quantity)
                val newTotals = calculateOrderTotals(newCart)
                updateState { it.copy(cart = newCart, totals = newTotals) }
            }
            CatalogIntent.ClearCart -> {
                updateState { it.copy(cart = emptyList(), totals = OrderTotals.EMPTY) }
            }
            CatalogIntent.Checkout -> {
                val s = state.value
                if (s.cart.isEmpty()) {
                    emitEffect(CatalogEffect.ShowError("Cart is empty"))
                } else {
                    emitEffect(CatalogEffect.NavigateToCheckout(s.cart, s.totals))
                }
            }
        }
    }
}
