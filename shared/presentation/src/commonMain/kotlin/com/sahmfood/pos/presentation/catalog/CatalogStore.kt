package com.sahmfood.pos.presentation.catalog

import com.sahmfood.pos.domain.entities.OrderTotals
import com.sahmfood.pos.domain.usecases.CalculateOrderTotals
import com.sahmfood.pos.domain.usecases.ClearCart
import com.sahmfood.pos.domain.usecases.GetProductCatalog
import com.sahmfood.pos.domain.usecases.ObserveCart
import com.sahmfood.pos.domain.usecases.RemoveCartItem
import com.sahmfood.pos.domain.usecases.SetCartItemQuantity
import com.sahmfood.pos.domain.usecases.SnapshotCart
import com.sahmfood.pos.presentation.common.BaseStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * CatalogStore now talks only to use cases — no repository imports.
 *
 * Cart state is sourced from the persisted [ObserveCart] flow which
 * joins the persisted cart lines with the live product catalog. Every
 * cart mutation goes through a write-side use case which persists to
 * Room; the resulting Room flow update then re-emits the cart via
 * ObserveCart, closing the loop.
 */
class CatalogStore(
    private val getProductCatalog: GetProductCatalog,
    private val observeCart: ObserveCart,
    private val setCartItemQuantity: SetCartItemQuantity,
    private val removeCartItem: RemoveCartItem,
    private val clearCart: ClearCart,
    private val snapshotCart: SnapshotCart,
    private val calculateOrderTotals: CalculateOrderTotals,
) : BaseStore<CatalogState, CatalogIntent, CatalogEffect>(CatalogState()) {

    init {
        scope.launch {
            getProductCatalog()
                .catch { t ->
                    updateState {
                        it.copy(isLoading = false, errorMessage = t.message ?: "load failed")
                    }
                }
                .collect { products ->
                    val categories = products.map { it.category }.distinct()
                    updateState {
                        it.copy(
                            products = products,
                            categories = categories,
                            isLoading = false,
                        )
                    }
                }
        }
        scope.launch {
            observeCart().collect { items ->
                val totals = calculateOrderTotals(items)
                updateState { it.copy(cart = items, totals = totals) }
            }
        }
    }

    override suspend fun handle(intent: CatalogIntent) {
        when (intent) {
            CatalogIntent.LoadCatalog -> {
                updateState { it.copy(isLoading = true, errorMessage = null) }
                // Flow is already collected in init.
            }
            is CatalogIntent.SetSearchQuery -> updateState { it.copy(searchQuery = intent.query) }
            is CatalogIntent.SelectCategory -> updateState { it.copy(selectedCategory = intent.category) }
            is CatalogIntent.AddToCart -> {
                val current = snapshotCart()
                val existing = current.firstOrNull { it.productId == intent.product.id }
                val newQty = (existing?.quantity ?: 0) + 1
                setCartItemQuantity(intent.product.id, newQty)
                emitEffect(CatalogEffect.ProductAdded(intent.product))
            }
            is CatalogIntent.RemoveFromCart -> removeCartItem(intent.productId)
            is CatalogIntent.UpdateQuantity -> {
                setCartItemQuantity(intent.productId, intent.quantity)
            }
            CatalogIntent.ClearCart -> {
                clearCart()
                updateState { it.copy(totals = OrderTotals.EMPTY) }
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
