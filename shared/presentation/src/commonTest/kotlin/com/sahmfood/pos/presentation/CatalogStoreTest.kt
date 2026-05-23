package com.sahmfood.pos.presentation

import app.cash.turbine.test
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.PersistedCartLine
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.domain.repositories.CartRepository
import com.sahmfood.pos.domain.repositories.ProductRepository
import com.sahmfood.pos.domain.usecases.CalculateOrderTotals
import com.sahmfood.pos.domain.usecases.ClearCart
import com.sahmfood.pos.domain.usecases.GetProductCatalog
import com.sahmfood.pos.domain.usecases.ObserveCart
import com.sahmfood.pos.domain.usecases.RemoveCartItem
import com.sahmfood.pos.domain.usecases.SetCartItemQuantity
import com.sahmfood.pos.domain.usecases.SnapshotCart
import com.sahmfood.pos.presentation.catalog.CatalogEffect
import com.sahmfood.pos.presentation.catalog.CatalogIntent
import com.sahmfood.pos.presentation.catalog.CatalogStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CatalogStoreTest {
    private val sampleProducts = listOf(
        Product("a", "Beef", Money(8500), "Burgers", null),
        Product("b", "Margherita", Money(12000), "Pizza", null),
    )

    private class FakeProductRepository(private val products: List<Product>) : ProductRepository {
        override fun observeAll(): Flow<List<Product>> = flowOf(products)
        override suspend fun getById(id: String): Product? = products.firstOrNull { it.id == id }
        override suspend fun upsertAll(products: List<Product>) {}
    }

    private class FakeCartRepository : CartRepository {
        private val state = MutableStateFlow<List<PersistedCartLine>>(emptyList())
        override fun observe(): Flow<List<PersistedCartLine>> = state.asStateFlow()
        override suspend fun snapshot(): List<PersistedCartLine> = state.value
        override suspend fun setQuantity(productId: String, quantity: Int) {
            state.value = if (quantity <= 0) {
                state.value.filterNot { it.productId == productId }
            } else {
                val existing = state.value.firstOrNull { it.productId == productId }
                if (existing != null) {
                    state.value.map {
                        if (it.productId == productId) it.copy(quantity = quantity) else it
                    }
                } else {
                    state.value + PersistedCartLine(productId, quantity, 0L)
                }
            }
        }
        override suspend fun remove(productId: String) {
            state.value = state.value.filterNot { it.productId == productId }
        }
        override suspend fun clear() { state.value = emptyList() }
    }

    private fun newStore(): CatalogStore {
        val productRepo = FakeProductRepository(sampleProducts)
        val cartRepo = FakeCartRepository()
        return CatalogStore(
            getProductCatalog = GetProductCatalog(productRepo),
            observeCart = ObserveCart(cartRepo, productRepo),
            setCartItemQuantity = SetCartItemQuantity(cartRepo),
            removeCartItem = RemoveCartItem(cartRepo),
            clearCart = ClearCart(cartRepo),
            snapshotCart = SnapshotCart(cartRepo),
            calculateOrderTotals = CalculateOrderTotals(),
        )
    }

    @Test
    fun `initial load populates products and categories`() = runTest {
        val store = newStore()
        store.state.test {
            var loaded = awaitItem()
            while (loaded.products.isEmpty()) loaded = awaitItem()
            assertEquals(2, loaded.products.size)
            assertEquals(listOf("Burgers", "Pizza"), loaded.categories)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `add to cart persists and recomputes totals`() = runTest {
        val store = newStore()
        store.effects.test {
            store.state.test {
                var s = awaitItem()
                while (s.products.isEmpty()) s = awaitItem()
                cancelAndIgnoreRemainingEvents()
            }
            store.dispatch(CatalogIntent.AddToCart(sampleProducts.first()))
            val effect = awaitItem()
            assertTrue(effect is CatalogEffect.ProductAdded)
            cancelAndIgnoreRemainingEvents()
        }
        store.state.test {
            var s = awaitItem()
            while (s.cart.isEmpty()) s = awaitItem()
            assertEquals(1, s.cart.size)
            assertEquals(8500, s.totals.subtotal.amount)
            assertEquals(1190, s.totals.taxAmount.amount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `checkout on empty cart emits error`() = runTest {
        val store = newStore()
        store.effects.test {
            store.dispatch(CatalogIntent.Checkout)
            val effect = awaitItem()
            assertTrue(effect is CatalogEffect.ShowError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filter by category narrows products`() = runTest {
        val store = newStore()
        store.state.test {
            var s = awaitItem()
            while (s.products.isEmpty()) s = awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
        store.dispatch(CatalogIntent.SelectCategory("Pizza"))
        store.state.test {
            var s = awaitItem()
            while (s.selectedCategory != "Pizza") s = awaitItem()
            assertEquals(1, s.filteredProducts.size)
            assertEquals("b", s.filteredProducts.first().id)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
